package com.github.zerobranch.beebox.home

import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import com.github.zerobranch.beebox.commons_java.delegates.fresh
import com.github.zerobranch.beebox.domain.models.TrainingType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : BaseMainViewModel() {
    override val screenName: String = "Home"

    private val _homeState = MutableStateFlow<List<HomeState>>(emptyList())
    val homeState = _homeState.asStateFlow()

    private val _action = OnceFlow<HomeAction>()
    val action = _action.asSharedFlow()

    init {
        _homeState.fresh {
            mutableListOf<HomeState>().apply {
                add(HomeState.RecentTraining)
                addAll(TrainingType.entries.map { HomeState.Training(it) })
            }
        }
    }

    fun onTrainingClick(trainingType: TrainingType) {
        _action.tryEmit(HomeAction.GoToTrainingSettingsDialog(trainingType))
    }

    fun onRecentTrainingsClick() {
        _action.tryEmit(HomeAction.GoToRecentTrainingDialog)
    }
}