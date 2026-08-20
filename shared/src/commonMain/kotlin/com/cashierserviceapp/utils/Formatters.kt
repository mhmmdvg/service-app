package com.cashierserviceapp.utils

import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.date_today
import cashierserviceapp.shared.generated.resources.date_yesterday
import cashierserviceapp.shared.generated.resources.month_1
import cashierserviceapp.shared.generated.resources.month_10
import cashierserviceapp.shared.generated.resources.month_11
import cashierserviceapp.shared.generated.resources.month_12
import cashierserviceapp.shared.generated.resources.month_2
import cashierserviceapp.shared.generated.resources.month_3
import cashierserviceapp.shared.generated.resources.month_4
import cashierserviceapp.shared.generated.resources.month_5
import cashierserviceapp.shared.generated.resources.month_6
import cashierserviceapp.shared.generated.resources.month_7
import cashierserviceapp.shared.generated.resources.month_8
import cashierserviceapp.shared.generated.resources.month_9
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
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

/** 24-hour clock, `09:05`. Digits only, so no catalog lookup is needed. */
fun LocalDateTime.formatTime(): String =
    "${hour.padded()}:${minute.padded()}"

/**
 * `13 Aug 2026`.
 *
 * Suspending because the month name comes out of the string catalog: these formatters run in
 * mappers on the way to a UI model, not inside composition, so [getString] is the only way to reach
 * the same `values-<tag>` catalog a `stringResource` would.
 */
suspend fun LocalDate.formatDate(): String = "$day ${month.shortName()} $year"

/** `13 Aug` — for dates recent enough that the year is noise. */
suspend fun LocalDate.formatDayMonth(): String = "$day ${month.shortName()}"

/**
 * How a list row states when something happened, in the space of a few characters: a clock time if
 * it was today, a word if it was yesterday, otherwise a date — dropping the year until it's a
 * different one.
 *
 * Takes [today] rather than reading the clock so callers can compute it once per load instead of
 * once per row. Returns "" for a timestamp that didn't parse, so a row renders without a stray
 * placeholder.
 */
suspend fun formatRelativeTimestamp(iso: String, today: LocalDate): String {
    val moment = parseTimestamp(iso)?.toLocalDateTime() ?: return ""
    val date = moment.date

    return when {
        date == today -> moment.formatTime()
        date.toEpochDays() == today.toEpochDays() - 1 -> getString(Res.string.date_yesterday)
        date.year == today.year -> date.formatDayMonth()
        else -> date.formatDate()
    }
}

/** `13 August 2026`. */
suspend fun LocalDate.formatLongDate(): String = "$day ${month.fullName()} $year"

private fun Int.padded(): String = toString().padStart(2, '0')

/**
 * Three letters is the right width for a list row in both languages the app ships — `Aug`/`Agu`,
 * `Dec`/`Des` — so the short form is a trim of the full one rather than a second set of strings.
 */
private suspend fun kotlinx.datetime.Month.shortName(): String = fullName().take(3)

private suspend fun kotlinx.datetime.Month.fullName(): String = getString(monthNames[ordinal])

private val monthNames: List<StringResource> = listOf(
    Res.string.month_1,
    Res.string.month_2,
    Res.string.month_3,
    Res.string.month_4,
    Res.string.month_5,
    Res.string.month_6,
    Res.string.month_7,
    Res.string.month_8,
    Res.string.month_9,
    Res.string.month_10,
    Res.string.month_11,
    Res.string.month_12,
)
