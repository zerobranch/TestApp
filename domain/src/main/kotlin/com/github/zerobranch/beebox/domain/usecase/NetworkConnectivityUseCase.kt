package com.github.zerobranch.beebox.domain.usecase

import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectivityUseCase @Inject constructor() {
    private val _connectionRestoredState = OnceFlow<Unit>()
    val connectionRestoredState = _connectionRestoredState.asSharedFlow()
    fun onConnectionRestored() = _connectionRestoredState.tryEmit(Unit)
}