package com.github.zerobranch.beebox.add_word_dialog

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import com.github.zerobranch.beebox.domain.models.BeeboxConfig
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.domain.usecase.DraftWordUseCase
import com.github.zerobranch.beebox.domain.usecase.WordUseCase
import com.github.zerobranch.beebox.logging.error
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

@SuppressLint("StaticFieldLeak")
class AddWordViewModel @AssistedInject constructor(
    private val draftWordUseCase: DraftWordUseCase,
    private val wordUseCase: WordUseCase,
    private val beeboxConfig: BeeboxConfig,
    @ApplicationContext private var context: Context,
    @Assisted private val isEditMode: Boolean
) : BaseMainViewModel() {

    override val screenName: String = "AddWord"

    private val _action = OnceFlow<AddWordAction>()
    val action = _action.asSharedFlow()

    private var player: MediaPlayer? = null

    val wordState = flow {
        emitAll(
            draftWordUseCase.draftWordState.map {
                WordState(
                    draftWord = it,
                    isEditMode = isEditMode
                )
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    override fun onCleared() {
        super.onCleared()
        player?.release()
        player = null
    }

    fun onPause() {
        if (player?.isPlaying == true) {
            player?.pause()
        }
    }

    fun onWordChanged(word: String) {
        draftWordUseCase.onDraftWordStateChange(word = word)
    }

    fun onTranslateChanged(translate: String) {
        draftWordUseCase.onDraftWordStateChange(translates = translate)
    }

    fun onHintChanged(id: String, text: String) {
        draftWordUseCase.onHintChanged(id, text)
    }

    fun onWordTypeDeleteClick(wordType: WordType) {
        draftWordUseCase.onWordTypeChanged(wordType.copy(isSelected = false))
    }

    fun onCategoryDeleteClick(category: WordCategory) {
        draftWordUseCase.onCategoryChanged(category.copy(isSelected = false))
    }

    fun onAddWordParentClick() {
        _action.tryEmit(AddWordAction.GoToLocalSearch)
    }

    fun onDeleteWordParentClick() {
        draftWordUseCase.onDraftWordStateChange(parentWord = null)
    }

    fun onWebClick(word: String) {
        _action.tryEmit(AddWordAction.OpenInBrowser(beeboxConfig.websiteUrl(word)))
    }

    fun onWordParentSelected(word: Word) {
        draftWordUseCase.onDraftWordStateChange(parentWord = word)
    }

    fun onAddWordCategoryClick() {
        _action.tryEmit(AddWordAction.GoToChooseCategory)
    }

    fun onWordCategoryClick() {
        _action.tryEmit(AddWordAction.GoToChooseCategory)
    }

    fun onWordTypeClick() {
        _action.tryEmit(AddWordAction.GoToChooseWordType)
    }

    fun onAddWordTypeClick() {
        _action.tryEmit(AddWordAction.GoToChooseWordType)
    }

    fun addHintClick() {
        draftWordUseCase.onAddHint()
    }

    fun addTranscriptionClick() {
        draftWordUseCase.onAddTranscription()
    }

    fun onFirstTranscriptionChanged(id: String, firstTranscription: String) {
        draftWordUseCase.onTranscriptionChange(id, firstTranscription = firstTranscription)
    }

    fun onFirstTranscriptionHintChanged(id: String, firstTranscriptionHint: String) {
        draftWordUseCase.onTranscriptionChange(id, firstTranscriptionHint = firstTranscriptionHint)
    }

    fun onSecondTranscriptionHintChanged(id: String, secondTranscriptionHint: String) {
        draftWordUseCase.onTranscriptionChange(id, secondTranscriptionHint = secondTranscriptionHint)
    }

    fun onSecondTranscriptionChanged(id: String, secondTranscription: String) {
        draftWordUseCase.onTranscriptionChange(id, secondTranscription = secondTranscription)
    }

    fun onTranscriptionTitleChanged(id: String, transcriptionTitle: String) {
        draftWordUseCase.onTranscriptionChange(id, title = transcriptionTitle)
    }

    fun onCommentChanged(comment: String) {
        draftWordUseCase.onDraftWordStateChange(comment = comment)
    }

    fun onTranscriptionDeleteClick(id: String) {
        draftWordUseCase.onDeleteTranscription(id)
    }

    fun onTranscriptionAudioClick(audio: String) {
        if (audio.isNotBlank()) {
            playAudio(beeboxConfig.baseUrl.plus(audio))
        }
    }

    fun onHintDeleteClick(id: String) {
        draftWordUseCase.onDeleteHint(id)
    }

    fun onApplyClick() {
        val draft = draftWordUseCase.draftWord
        if (draft.word.isNotBlank() && draft.translates.isNotEmpty()) {
            if (isEditMode) {
                wordUseCase.change(draft)
                    .onIO()
                    .onEach { _action.tryEmit(AddWordAction.Exit) }
                    .catch { th ->
                        _action.tryEmit(AddWordAction.BaseError)
                        javaClass.error(screenName, th, "change word failed")
                    }
                    .inViewModel()
            } else {
                wordUseCase.create(draft)
                    .onIO()
                    .onEach { _action.tryEmit(AddWordAction.Exit) }
                    .catch { th ->
                        _action.tryEmit(AddWordAction.BaseError)
                        javaClass.error(screenName, th, "create word failed")
                    }
                    .inViewModel()
            }
        } else {
            _action.tryEmit(AddWordAction.ValidationError)
        }
    }

    private fun playAudio(soundUrl: String) {
        runCatching {
            with(getOrCreateMediaPlayer()) {
                reset()
                setDataSource(context, Uri.parse(soundUrl))
                prepareAsync()
                setOnPreparedListener { player -> player.start() }
            }
        }.onFailure { th ->
            javaClass.error(screenName, th, "playAudio failed")
        }
    }

    private fun getOrCreateMediaPlayer(): MediaPlayer {
        if (player == null) {
            player = MediaPlayer()
            player?.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        }

        return player as MediaPlayer
    }
}