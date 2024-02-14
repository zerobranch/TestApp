package com.github.zerobranch.beebox.home

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.github.zerobranch.beebox.commons_android.utils.delegates.groupAdapter
import com.github.zerobranch.beebox.commons_android.utils.ext.launchWhenCreated
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.github.zerobranch.beebox.commons_app.base.BaseMainFragment
import com.github.zerobranch.beebox.home.databinding.FragmentHomeBinding
import com.github.zerobranch.beebox.home.items.RecentTrainingsItem
import com.github.zerobranch.beebox.home.items.TrainingTitleItem
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseMainFragment(R.layout.fragment_home) {
    @Inject
    internal lateinit var navProvider: HomeNavProvider

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val viewModel: HomeViewModel by viewModels()
    private val homeAdapter: GroupieAdapter by groupAdapter { binding.rvTraining }

    override fun initUi() {
        super.initUi()
        initRvTraining()
        initEdgeToEdgeMode()
    }

    override fun observeOnStates() {
        viewModel.homeState
            .onEach { items ->
                homeAdapter.updateAsync(
                    items.map { item ->
                        when (item) {
                            is HomeState.RecentTraining -> RecentTrainingsItem()
                            is HomeState.Training -> TrainingTitleItem(item.training)
                        }
                    }
                )
            }
            .launchWhenCreated()

        viewModel.action
            .onEach { action ->
                when (action) {
                    is HomeAction.GoToTrainingSettingsDialog -> {
                        navigate(navProvider.toTrainingSettingsDialog(action.trainingType))
                    }

                    is HomeAction.GoToRecentTrainingDialog -> {
                        navigate(navProvider.toRecentTrainingsDialog)
                    }
                }
            }
            .launchWhenCreated()
    }

    private fun initRvTraining() = with(binding.rvTraining) {
        layoutManager = GridLayoutManager(requireContext(), 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int =
                    if (homeAdapter.getItem(position) is RecentTrainingsItem) 2 else 1
            }
        }

        adapter = homeAdapter
        homeAdapter.setOnItemClickListener { item, _ ->
            when (item) {
                is TrainingTitleItem -> viewModel.onTrainingClick(item.trainingType)
                is RecentTrainingsItem -> viewModel.onRecentTrainingsClick()
            }
        }
    }

    private fun initEdgeToEdgeMode() {
        binding.rvTraining.applyInsetter {
            type(navigationBars = true, statusBars = true) { padding(top = true, bottom = true) }
        }
    }
}