package com.github.zerobranch.beebox.data.base

import com.github.zerobranch.beebox.domain.models.exceptions.ExistChildWordException
import com.github.zerobranch.beebox.domain.models.exceptions.HttpNetException
import com.github.zerobranch.beebox.domain.models.exceptions.NetworkException
import com.github.zerobranch.beebox.domain.models.exceptions.NoInternetException
import com.github.zerobranch.beebox.services.work.NetworkWorkerLauncher
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

class NetworkErrorParser @Inject constructor(
    private val networkWorkerLauncher: NetworkWorkerLauncher
) {
    operator fun invoke(th: Throwable) {
        throw when (th) {
            is HttpException -> HttpNetException(th.code(), th.message(), th)
            is UnknownHostException -> {
                networkWorkerLauncher.launch()
                NoInternetException(th)
            }
            is NetworkException -> th
            is ExistChildWordException -> th
            else -> IOException(th)
        }
    }
}
