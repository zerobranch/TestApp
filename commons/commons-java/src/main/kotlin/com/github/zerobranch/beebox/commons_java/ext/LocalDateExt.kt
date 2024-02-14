package com.github.zerobranch.beebox.commons_java.ext

import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

val Long.mskLocalDateTime: LocalDateTime
    get() = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.of("Europe/Moscow"))

val LocalDateTime.moscowDate: ZonedDateTime?
    get() = atZone(ZoneId.of("Europe/Moscow"))

val LocalDate.isToday
    get() = this == LocalDate.now()

val LocalDate.isCurrentYear
    get() = this.year == LocalDate.now().year

fun ZonedDateTime.stringWithFormat(format: String): String? {
    return try {
        this.format(DateTimeFormatter.ofPattern(format))
    } catch (ex: Exception) {
        null
    }
}

fun LocalDateTime.stringWithFormat(format: String): String? {
    return try {
        this.format(DateTimeFormatter.ofPattern(format))
    } catch (ex: Exception) {
        null
    }
}

fun LocalDate.stringWithFormat(format: String): String? {
    return try {
        this.format(DateTimeFormatter.ofPattern(format))
    } catch (ex: Exception) {
        null
    }
}

val ZonedDateTime.millis
    get() = toInstant().toEpochMilli()

val LocalDate.millis
    get() = atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

val LocalDate.seconds
    get() = TimeUnit.MILLISECONDS.toSeconds(atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli())

fun parseZonedDateTime(value: String, vararg patterns: String): ZonedDateTime? {
    patterns.forEach { pattern ->
        val result = runCatching { parseZonedDateTimeInternal(value, pattern) }
        if (result.isSuccess) {
            return result.getOrNull()
        }
    }

    return null
}

private fun parseZonedDateTimeInternal(value: String, pattern: String): ZonedDateTime? =
    ZonedDateTime.parse(value, DateTimeFormatter.ofPattern(pattern))

fun parseDateTime(value: String, pattern: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(value, DateTimeFormatter.ofPattern(pattern))
    } catch (ex: Exception) {
        null
    }
}

fun parseDate(value: String, pattern: String): LocalDate? {
    return try {
        LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern))
    } catch (ex: Exception) {
        null
    }
}

val Long.zonedDateTime: ZonedDateTime
    get() = ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())

val today: LocalDate
    get() = LocalDate.now()

val tomorrow: LocalDate
    get() = LocalDate.now().plusDays(1)

val dayAfterTomorrow: LocalDate
    get() = LocalDate.now().plusDays(2)

val afterDayAfterTomorrow: LocalDate
    get() = LocalDate.now().plusDays(3)

fun prevMonth(): LocalDateTime = LocalDate.now().atStartOfDay().minusMonths(1)
