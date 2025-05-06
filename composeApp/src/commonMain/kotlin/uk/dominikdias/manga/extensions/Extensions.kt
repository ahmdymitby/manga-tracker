package uk.dominikdias.manga.extensions

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

fun Long?.toLocalDate(): LocalDate? {
    return this?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
    }
}

fun LocalDate?.toEpochMillis(): Long? {
    return this?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()
}