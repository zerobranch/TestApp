package com.github.zerobranch.beebox.training

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import com.github.zerobranch.beebox.domain.models.BeeboxConfig
import com.github.zerobranch.beebox.domain.models.training.ProgressTraining
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.domain.usecase.DraftWordUseCase
import com.github.zerobranch.beebox.domain.usecase.TrainingUseCase
import com.github.zerobranch.beebox.logging.error
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

@SuppressLint("StaticFieldLeak")
class TrainingViewModel @AssistedInject constructor(
    private val trainingUseCase: TrainingUseCase,
    private val draftWordUseCase: DraftWordUseCase,
    private val beeboxConfig: BeeboxConfig,
    @ApplicationContext private var context: Context,
    @Assisted private val progressTraining: ProgressTraining
) : BaseMainViewModel() {

    override val screenName: String = "Training"

    private val _action = OnceFlow<TrainingAction>()
    val action = _action.asSharedFlow()

    val uiState = flow {
        emit(progressTraining)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private var player: MediaPlayer? = null

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

    fun onEditClick(word: Word) {
        draftWordUseCase.reset().set(word)
        _action.tryEmit(TrainingAction.OpenEditWordDialog)
    }

    fun onTranscriptionAudioClick(audio: String) {
        if (audio.isNotBlank()) {
            playAudio(beeboxConfig.baseUrl.plus(audio))
        }
    }

    fun onPositionChange(position: Int) {
        val data = uiState.value?.copy(position = position) ?: return
        trainingUseCase
            .updateTraining(data)
            .onIO()
            .catch { th -> javaClass.error(screenName, th, "updateTraining failed") }
            .inViewModel()
    }

    private fun playAudio(soundUrl: String) {
        runCatching {
            with(getOrCreateMediaPlayer()) {
                reset()
                setDataSource(context, soundUrl.toUri())
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
