package com.github.zerobranch.beebox.domain.repository

import kotlinx.coroutines.flow.Flow

interface DeviceInfoRepository {
    val deviceId: Flow<String>
}