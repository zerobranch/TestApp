package com.github.zerobranch.beebox.data.source.local.serializers

import androidx.datastore.core.Serializer
import com.github.zerobranch.beebox.domain.models.AppSettings
import com.github.zerobranch.beebox.logging.wtf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object AppSettingsSerializer : Serializer<AppSettings> {
    private const val TAG = "AppSettingsSerializer"

    override val defaultValue: AppSettings
        get() = AppSettings()

    override suspend fun readFrom(
        input: InputStream
    ): AppSettings = runCatching {
        Json.decodeFromString(
            deserializer = AppSettings.serializer(),
            string = input.readBytes().decodeToString()
        )
    }
        .onFailure { th -> javaClass.wtf(TAG, th, "AppSettingsSerializer failed") }
        .getOrDefault(defaultValue)

    override suspend fun writeTo(
        t: AppSettings,
        output: OutputStream
    ): Unit = withContext(Dispatchers.IO) {
        output.write(
            Json.encodeToString(
                serializer = AppSettings.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}