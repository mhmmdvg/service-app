package com.cashierserviceapp.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * Rupiah, grouped the Indonesian way — `Rp 1.500.000`. Hand-rolled because there's no locale-aware
 * number formatter in common code, and the app only ever prints one currency.
 */
fun formatRupiah(amount: Long): String = "Rp ${groupDigits(amount)}"

private fun groupDigits(amount: Long): String {
    val negative = amount < 0
    val digits = amount.toString().removePrefix("-")

    val grouped = buildString {
        digits.forEachIndexed { index, char ->
            // Separators land wherever the remaining digit count is a clean multiple of three.
            if (index > 0 && (digits.length - index) % 3 == 0) append('.')
            append(char)
        }
    }

    return if (negative) "-$grouped" else grouped
}

/** Parses a server timestamp (`2026-08-13T17:16:47Z`), or null if it isn't one. */
fun parseTimestamp(iso: String): Instant? = runCatching { Instant.parse(iso) }.getOrNull()

fun Instant.toLocalDateTime(): LocalDateTime = toLocalDateTime(TimeZone.currentSystemDefault())

/** 24-hour clock, `09:05`. */
fun LocalDateTime.formatTime(): String =
    "${hour.padded()}:${minute.padded()}"

/** `13 Aug 2026`. */
fun LocalDate.formatDate(): String = "$day ${month.shortName()} $year"

/** `13 Aug` — for dates recent enough that the year is noise. */
fun LocalDate.formatDayMonth(): String = "$day ${month.shortName()}"

/**
 * How a list row states when something happened, in the space of a few characters: a clock time if
 * it was today, a word if it was yesterday, otherwise a date — dropping the year until it's a
 * different one.
 *
 * Takes [today] rather than reading the clock so callers can compute it once per load instead of
 * once per row. Returns "" for a timestamp that didn't parse, so a row renders without a stray
 * placeholder.
 */
fun formatRelativeTimestamp(iso: String, today: LocalDate): String {
    val moment = parseTimestamp(iso)?.toLocalDateTime() ?: return ""
    val date = moment.date

    return when {
        date == today -> moment.formatTime()
        date.toEpochDays() == today.toEpochDays() - 1 -> "Yesterday"
        date.year == today.year -> date.formatDayMonth()
        else -> date.formatDate()
    }
}

/** `13 August 2026`. */
fun LocalDate.formatLongDate(): String = "$day ${month.fullName()} $year"

private fun Int.padded(): String = toString().padStart(2, '0')

private fun kotlinx.datetime.Month.shortName(): String = fullName().take(3)

private fun kotlinx.datetime.Month.fullName(): String = when (ordinal) {
    0 -> "January"
    1 -> "February"
    2 -> "March"
    3 -> "April"
    4 -> "May"
    5 -> "June"
    6 -> "July"
    7 -> "August"
    8 -> "September"
    9 -> "October"
    10 -> "November"
    else -> "December"
}
