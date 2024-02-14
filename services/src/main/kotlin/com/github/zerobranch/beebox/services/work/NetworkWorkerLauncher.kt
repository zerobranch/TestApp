package com.github.zerobranch.beebox.services.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkWorkerLauncher @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private companion object {
        const val TAG = "NetworkWorkerLauncher"
    }

    fun launch() {
        val workRequest = OneTimeWorkRequestBuilder<NetworkWorker>()
            .setConstraints(
                Constraints
                    .Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(TAG)
            .build()

        with(WorkManager.getInstance(context)) {
            cancelAllWorkByTag(TAG)
            enqueue(workRequest)
        }
    }
}