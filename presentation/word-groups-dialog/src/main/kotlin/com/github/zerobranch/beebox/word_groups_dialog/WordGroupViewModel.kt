package com.github.zerobranch.beebox.word_groups_dialog

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import com.github.zerobranch.beebox.commons_java.delegates.fresh
import com.github.zerobranch.beebox.commons_java.ext.indexOfOrNull
import com.github.zerobranch.beebox.commons_java.ext.replace
import com.github.zerobranch.beebox.domain.models.BeeboxConfig
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.models.words.DraftWord
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.domain.usecase.DraftWordUseCase
import com.github.zerobranch.beebox.domain.usecase.WordUseCase
import com.github.zerobranch.beebox.logging.error
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach

@SuppressLint("StaticFieldLeak")
class WordGroupViewModel @AssistedInject constructor(
    private val draftWordUseCase: DraftWordUseCase,
    private val wordUseCase: WordUseCase,
    private val beeboxConfig: BeeboxConfig,
    @ApplicationContext private var context: Context,
    @Assisted private val drafts: MutableList<DraftWord>,
) : BaseMainViewModel() {

    override val screenName: String = "WordGroup"

    private val _state = MutableStateFlow<WordGroupState?>(null)
    val state = _state.asStateFlow()

    private val _action = OnceFlow<WordGroupAction>()
    val action = _action.asSharedFlow()

    private var player: MediaPlayer? = null
    private var currentItemPosition = 0

    init {
        drafts.replace(
            drafts.map { draft ->
                draft.copy(
                    wordTypes = draftWordUseCase.wordTypes,
                    categories = draftWordUseCase.wordCategories
                )
            }
        )

        draftWordUseCase.reset().set(drafts.first())

        draftWordUseCase.draftWordState
            .onEach { changedDraft ->
                drafts.replace(
                    drafts.map { draft ->
                        if (draft.word == changedDraft.word) {
                            changedDraft
                        } else {
                            draft
                        }
                    }
                )

                updateUi()
            }
            .inViewModel()
    }

    override fun onCleared() {
        super.onCleared()
        draftWordUseCase.reset()
        player?.release()
        player = null
    }

    fun onPause() {
        if (player?.isPlaying == true) {
            player?.pause()
        }
    }

    fun onNextClick() {
        val old = _state.value?.draft ?: return
        val oldIndex = drafts.indexOfOrNull(old) ?: return
        val currentItem = drafts.getOrNull(oldIndex + 1) ?: return
        currentItemPosition = oldIndex + 1
        val isExistNextItem = drafts.indexOf(currentItem) < drafts.size - 1

        _state.fresh {
            WordGroupState(
                draft = currentItem,
                controlState = if (isExistNextItem) ControlState.MIDDLE else ControlState.END
            )
        }

        draftWordUseCase.set(currentItem)
    }

    fun onPreviousClick() {
        val oldItem = _state.value?.draft ?: return
        val oldIndex = drafts.indexOfOrNull(oldItem) ?: return
        val currentItem = drafts.getOrNull(oldIndex - 1) ?: return
        currentItemPosition = oldIndex - 1
        val isExistPrevItem = drafts.indexOf(currentItem) > 0

        _state.fresh {
            WordGroupState(
                draft = currentItem,
                controlState = if (isExistPrevItem) ControlState.MIDDLE else ControlState.START
            )
        }

        draftWordUseCase.set(currentItem)
    }

    fun onSaveClick() {
        drafts.forEachIndexed { index, draft ->
            if (draft.word.isBlank() || draft.translates.isBlank()) {
                currentItemPosition = index
                updateUi()
                return
            }
        }

        wordUseCase.createAll(drafts)
            .onIO()
            .onEach { _action.tryEmit(WordGroupAction.Exit) }
            .catch { th ->
                _action.tryEmit(WordGroupAction.BaseError)
                javaClass.error(screenName, th, "createAll word failed")
            }
            .inViewModel()
    }

    fun onDeleteClick() {
        val draftWord = _state.value?.draft ?: return
        _action.tryEmit(WordGroupAction.OpenDeleteDialog(draftWord))
    }

    fun onCloseClick() {
        _action.tryEmit(WordGroupAction.OpenExitDialog)
    }

    fun onDialogDeleteClick(draftWord: DraftWord) {
        currentItemPosition = drafts.indexOfOrNull(draftWord) ?: 0
        drafts.remove(draftWord)
        updateUi()
    }

    fun onDialogDeleteClick() {
        exit()
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
        _action.tryEmit(WordGroupAction.GoToLocalSearch)
    }

    fun onDeleteWordParentClick() {
        draftWordUseCase.onDraftWordStateChange(parentWord = null)
    }

    fun onWebClick(word: String) {
        _action.tryEmit(WordGroupAction.OpenInBrowser(beeboxConfig.websiteUrl(word)))
    }

    fun onWordParentSelected(word: Word) {
        draftWordUseCase.onDraftWordStateChange(parentWord = word)
    }

    fun onAddWordCategoryClick() {
        _action.tryEmit(WordGroupAction.GoToChooseCategory)
    }

    fun onWordCategoryClick() {
        _action.tryEmit(WordGroupAction.GoToChooseCategory)
    }

    fun onWordTypeClick() {
        _action.tryEmit(WordGroupAction.GoToChooseWordType)
    }

    fun onAddWordTypeClick() {
        _action.tryEmit(WordGroupAction.GoToChooseWordType)
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

    private fun updateUi() {
        if (drafts.isEmpty()) {
            exit()
            return
        }

        val draftPosition = minOf(currentItemPosition, drafts.size - 1)
        val draft = drafts.getOrElse(draftPosition) { drafts.first() }
        _state.fresh {
            WordGroupState(
                draft = draft,
                controlState = when {
                    drafts.size == 1 -> ControlState.SINGLE
                    draftPosition == 0 -> ControlState.START
                    draftPosition == drafts.size - 1 -> ControlState.END
                    else -> ControlState.MIDDLE
                }
            )
        }

        draftWordUseCase.set(draft)
    }

    private fun exit() {
        draftWordUseCase.reset()
        _action.tryEmit(WordGroupAction.Exit)
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