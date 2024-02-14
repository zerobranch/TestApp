package com.github.zerobranch.beebox.data.source.db

import androidx.room.TypeConverter
import com.github.zerobranch.beebox.commons_java.ext.fromJsonList
import com.github.zerobranch.beebox.commons_java.ext.millis
import com.github.zerobranch.beebox.commons_java.ext.toJson
import com.github.zerobranch.beebox.commons_java.ext.zonedDateTime
import com.github.zerobranch.beebox.data.entity.HintEntity
import com.github.zerobranch.beebox.data.entity.TranscriptionEntity
import com.github.zerobranch.beebox.domain.models.TrainingType
import com.squareup.moshi.Moshi
import org.threeten.bp.ZonedDateTime

class ZonedDateTimeConverter {
    @TypeConverter
    fun from(value: ZonedDateTime?): Long? = value?.millis

    @TypeConverter
    fun to(value: Long?): ZonedDateTime? = value?.zonedDateTime
}

class HintConverter {
    private val moshi: Moshi = Moshi.Builder().build()

    @TypeConverter
    fun from(value: List<HintEntity>?): String? = value?.run { moshi.toJson(value) }

    @TypeConverter
    fun to(value: String?): List<HintEntity>? = value?.run { moshi.fromJsonList(value) }
}

class TranscriptionConverter {
    private val moshi: Moshi = Moshi.Builder().build()

    @TypeConverter
    fun from(value: List<TranscriptionEntity>?): String? = value?.run { moshi.toJson(value) }

    @TypeConverter
    fun to(value: String?): List<TranscriptionEntity>? = value?.run { moshi.fromJsonList(value) }
}

class TrainingTypeConverter {
    @TypeConverter
    fun from(value: TrainingType?): Int? = value?.id

    @TypeConverter
    fun to(id: Int?): TrainingType? = id?.run { TrainingType.parse(id) }
}
