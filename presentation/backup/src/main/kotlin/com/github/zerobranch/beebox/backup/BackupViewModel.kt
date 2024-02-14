package com.github.zerobranch.beebox.backup

import android.net.Uri
import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import com.github.zerobranch.beebox.commons_java.delegates.SingleSharedFlow
import com.github.zerobranch.beebox.commons_java.delegates.emitWhenSubscribed
import com.github.zerobranch.beebox.domain.models.BeeboxConfig
import com.github.zerobranch.beebox.domain.models.DBBackupInfo
import com.github.zerobranch.beebox.domain.models.DBSnapshotInfo
import com.github.zerobranch.beebox.domain.usecase.BackupUseCase
import com.github.zerobranch.beebox.domain.usecase.WordUseCase
import com.github.zerobranch.beebox.logging.error
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import org.threeten.bp.LocalDateTime

@Suppress("OPT_IN_USAGE")
class BackupViewModel @AssistedInject constructor(
    private val beeboxConfig: BeeboxConfig,
    private val backupUseCase: BackupUseCase,
    private val wordUseCase: WordUseCase,
    @Assisted private val backupInfo: DBBackupInfo?
) : BaseMainViewModel() {

    override val screenName: String = "Backup"

    private val _actions = OnceFlow<BackupAction>()
    val actions = _actions.asSharedFlow()

    private val _backupInfoState = SingleSharedFlow<DBBackupInfo?>()
    val backupInfoState = _backupInfoState.asSharedFlow()

    private var isTelegramChecked = true

    init {
        backupInfo?.let { backupInfo ->
            inViewModel {
                _backupInfoState.emitWhenSubscribed(backupInfo)
            }
        }
    }

    fun onUploadClick() {
        _actions.tryEmit(BackupAction.OpenFileChooser)
    }

    fun onDBFileUriReceived(uri: Uri) {
        backupUseCase
            .onDBFileUriReceived(uri.toString())
            .onIO()
            .onEach { backupInfo -> _backupInfoState.tryEmit(backupInfo) }
            .catch { th ->
                javaClass.error(screenName, th, "onDBFileUriReceived failed")
                _actions.tryEmit(BackupAction.OpeningDBError)
            }
            .inViewModel()
    }

    fun onTelegramCheckedChanged(checked: Boolean) {
        isTelegramChecked = checked
    }

    fun onApplyClick(backupInfo: DBBackupInfo) {
        backupUseCase
            .importDB(backupInfo)
            .onIO()
            .onEach { _actions.tryEmit(BackupAction.Rebirth) }
            .catch { th ->
                javaClass.error(screenName, th, "importDB failed")
                _actions.tryEmit(BackupAction.CopyingDBError)
            }
            .inViewModel()
    }

    fun onSaveClick() {
        wordUseCase
            .getWordTypesCount()
            .zip(wordUseCase.getWordCategoriesCount()) { types, categories -> types to categories }
            .zip(wordUseCase.getWordCount()) { (types, categories), words ->
                DBSnapshotInfo(
                    wordsCount = words,
                    wordTypesCount = types,
                    wordCategoriesCount = categories,
                    dbUri = ""
                )
            }
            .flatMapConcat { dbSnapshot ->
                backupUseCase
                    .getDatabaseFile()
                    .map { dbUri -> dbSnapshot.copy(dbUri = dbUri) }
            }
            .onIO()
            .onEach { dbSnapshot ->
                _actions.tryEmit(
                    BackupAction.Share(
                        databaseUri = Uri.parse(dbSnapshot.dbUri),
                        targetPackage = beeboxConfig.telegramPackage,
                        date = LocalDateTime.now(),
                        dbVersion = beeboxConfig.databaseVersion,
                        typesCount = dbSnapshot.wordTypesCount,
                        categoriesCount = dbSnapshot.wordCategoriesCount,
                        wordsCount = dbSnapshot.wordsCount,
                        isTelegramChecked = isTelegramChecked
                    )
                )
            }
            .catch { th -> javaClass.error(screenName, th, "onSaveClick failed") }
            .inViewModel()
    }
}
