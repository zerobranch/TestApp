package com.github.zerobranch.beebox.training

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.zerobranch.beebox.commons_android.utils.delegates.args
import com.github.zerobranch.beebox.commons_android.utils.delegates.groupAdapter
import com.github.zerobranch.beebox.commons_android.utils.ext.attachSnapHelperWithListener
import com.github.zerobranch.beebox.commons_android.utils.ext.launchWhenCreated
import com.github.zerobranch.beebox.commons_android.utils.listeners.SnapOnScrollListener
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.github.zerobranch.beebox.commons_app.base.BaseMainFragment
import com.github.zerobranch.beebox.domain.models.TrainingType
import com.github.zerobranch.beebox.domain.models.training.ProgressTraining
import com.github.zerobranch.beebox.training.databinding.FragmentTrainingBinding
import com.github.zerobranch.beebox.training.items.AurallyTrainingItem
import com.github.zerobranch.beebox.training.items.EnterWordTrainingItem
import com.github.zerobranch.beebox.training.items.InEnglishTrainingItem
import com.github.zerobranch.beebox.training.items.InRussianTrainingItem
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class TrainingFragment : BaseMainFragment(R.layout.fragment_training) {
    companion object {
        const val PROGRESS_TRAINING_KEY = "progress_training_key"
    }

    @Inject
    internal lateinit var navProvider: TrainingNavProvider

    @Inject
    internal lateinit var factory: ViewModelFactory
    private val viewModel: TrainingViewModel by lazy { factory.get(trainingData) }

    private val binding by viewBinding(FragmentTrainingBinding::bind)
    private val trainingData by args<ProgressTraining>(PROGRESS_TRAINING_KEY)
    private val trainingAdapter: GroupieAdapter by groupAdapter { binding.rvTraining }

    override fun initUi() {
        super.initUi()
        hideNavigationBar(isSmooth = false)
        initRvTraining()
        initEdgeToEdgeMode()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showNavigationBar()
    }

    override fun observeOnStates() {
        viewModel.action
            .onEach { action ->
                when (action) {
                    is TrainingAction.OpenEditWordDialog -> navigate(navProvider.toEditWordScreen)
                }
            }
            .launchWhenCreated()

        viewModel.uiState
            .onEach { training ->
                training ?: return@onEach

                trainingAdapter.updateAsync(
                    training.trainingWords.mapNotNull { wordState ->
                        when (wordState.trainingType) {
                            TrainingType.IN_ENGLISH -> InEnglishTrainingItem(
                                word = wordState.word,
                                onEditClick = viewModel::onEditClick,
                                onTranscriptionAudioClick = viewModel::onTranscriptionAudioClick
                            )

                            TrainingType.IN_RUSSIAN -> InRussianTrainingItem(
                                word = wordState.word,
                                onEditClick = viewModel::onEditClick,
                                onTranscriptionAudioClick = viewModel::onTranscriptionAudioClick
                            )

                            TrainingType.AURALLY -> AurallyTrainingItem(
                                word = wordState.word,
                                onEditClick = viewModel::onEditClick,
                                onTranscriptionAudioClick = viewModel::onTranscriptionAudioClick
                            )

                            TrainingType.ENTER_WORD -> EnterWordTrainingItem(
                                word = wordState.word,
                                onEditClick = viewModel::onEditClick,
                                onTranscriptionAudioClick = viewModel::onTranscriptionAudioClick
                            )

                            else -> null
                        }
                    }
                )

                binding.rvTraining.scrollToPosition(training.position)
            }
            .launchWhenCreated()
    }

    private fun initRvTraining() = with(binding.rvTraining) {
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        adapter = trainingAdapter
        attachSnapHelperWithListener(
            snapHelper = PagerSnapHelper(),
            behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
            onSnapPositionChangeListener = object :
                SnapOnScrollListener.OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    viewModel.onPositionChange(position)
                }
            }
        )
    }

    private fun initEdgeToEdgeMode() {
        binding.rvTraining.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding(top = true, bottom = true)
            }
        }
    }
}
