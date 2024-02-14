package com.github.zerobranch.beebox.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.datastore.core.DataStore
import com.github.zerobranch.beebox.domain.models.AppSettings
import com.github.zerobranch.beebox.domain.repository.DeviceInfoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class DeviceInfoRepositoryImpl @Inject constructor(
    private val settingsDataStore: DataStore<AppSettings>,
    @ApplicationContext private val context: Context,
) : DeviceInfoRepository {

    override val deviceId: Flow<String>
        get() = settingsDataStore.data
            .map { it.deviceId }
            .map { deviceId ->
                deviceId ?: generateAppId().apply {
                    settingsDataStore.updateData { it.copy(deviceId = this) }
                }
            }

    @SuppressLint("HardwareIds")
    private fun generateAppId(): String =
        runCatching {
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }.getOrElse { UUID.randomUUID().toString() }
}
