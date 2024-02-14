package com.github.zerobranch.beebox.backup

import android.app.Activity
import androidx.core.view.isVisible
import com.github.zerobranch.beebox.backup.databinding.FragmentBackupBinding
import com.github.zerobranch.beebox.commons_android.utils.delegates.nullableArgs
import com.github.zerobranch.beebox.commons_android.utils.ext.getString
import com.github.zerobranch.beebox.commons_android.utils.ext.importFiles
import com.github.zerobranch.beebox.commons_android.utils.ext.launchWhenCreated
import com.github.zerobranch.beebox.commons_android.utils.ext.longToast
import com.github.zerobranch.beebox.commons_android.utils.ext.rebirth
import com.github.zerobranch.beebox.commons_android.utils.ext.registerActivityResults
import com.github.zerobranch.beebox.commons_android.utils.ext.share
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.github.zerobranch.beebox.commons_app.base.BaseMainFragment
import com.github.zerobranch.beebox.commons_java.ext.stringWithFormat
import com.github.zerobranch.beebox.domain.models.DBBackupInfo
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.github.zerobranch.beebox.commons_app.R as CommonR

@AndroidEntryPoint
class BackupFragment : BaseMainFragment(R.layout.fragment_backup) {
    companion object {
        const val DB_BACKUP_KEY = "db_backup_uri_key"
    }

    @Inject
    internal lateinit var factory: ViewModelFactory
    private val viewModel: BackupViewModel by lazy { factory.get(dbBackupUri) }

    private val binding by viewBinding(FragmentBackupBinding::bind)
    private val dbBackupUri by nullableArgs<DBBackupInfo>(DB_BACKUP_KEY)

    private val importFilesResultLauncher = registerActivityResults { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            return@registerActivityResults
        }

        val uri = result.data?.data ?: return@registerActivityResults
        viewModel.onDBFileUriReceived(uri)
    }

    override fun initUi() {
        super.initUi()
        initToolbar()
        initEdgeToEdgeMode()
        initClickListeners()
    }

    override fun observeOnStates() {
        super.observeOnStates()
        observeOnState()
        observeOnActions()
    }

    private fun initToolbar() = with(binding) {
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun initClickListeners() = with(binding) {
        btnExport.setOnClickListener { viewModel.onSaveClick() }
        btnImport.setOnClickListener { viewModel.onUploadClick() }
        cbToTelegram.setOnCheckedChangeListener { _, checked ->
            viewModel.onTelegramCheckedChanged(checked)
        }
    }

    private fun observeOnState() {
        viewModel.backupInfoState
            .onEach { backupInfo ->
                backupInfo ?: return@onEach
                binding.run {
                    llImportDbContainer.isVisible = true
                    btnApply.setOnClickListener { viewModel.onApplyClick(backupInfo) }
                    tvImportDbInfo.text = getString(
                        CommonR.string.common_backup_info,
                        backupInfo.dbVersion,
                        backupInfo.wordsCount,
                        backupInfo.wordCategoriesCount,
                        backupInfo.wordTypesCount,
                    )
                }
            }
            .launchWhenCreated()
    }

    private fun observeOnActions() {
        viewModel.actions
            .onEach { action ->
                when (action) {
                    is BackupAction.Share -> {
                        val date = action.date
                        share(
                            text = getString(
                                CommonR.string.common_database_share_text,
                                action.dbVersion,
                                date.stringWithFormat("dd MMM yyyy"),
                                date.stringWithFormat("HH:mm:ss"),
                                action.wordsCount,
                                action.categoriesCount,
                                action.typesCount
                            ),
                            targetPackage = action.targetPackage.takeIf { action.isTelegramChecked },
                            attachFiles = listOf(action.databaseUri)
                        )
                    }
                    BackupAction.OpenFileChooser -> {
                        importFilesResultLauncher.launch(
                            importFiles(getString(CommonR.string.common_choose_file))
                        )
                    }
                    BackupAction.OpeningDBError -> longToast(CommonR.string.common_error_opening_db_file)
                    BackupAction.CopyingDBError -> longToast(CommonR.string.common_error_copying_db_file)
                    BackupAction.Rebirth -> requireContext().rebirth()
                }
            }
            .launchWhenCreated()
    }

    private fun initEdgeToEdgeMode() = with(binding) {
        toolbar.applyInsetter {
            type(statusBars = true) { margin(top = true) }
        }
    }
}
