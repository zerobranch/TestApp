package com.github.zerobranch.beebox.services.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.github.zerobranch.beebox.domain.usecase.NetworkConnectivityUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NetworkWorker @AssistedInject constructor(
    private val useCase: NetworkConnectivityUseCase,
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        useCase.onConnectionRestored()
        return Result.success()
    }
}