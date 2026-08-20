package com.cashierserviceapp.localization

import androidx.compose.runtime.Composable
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.date_today
import cashierserviceapp.shared.generated.resources.device_count_one
import cashierserviceapp.shared.generated.resources.device_count_other
import cashierserviceapp.shared.generated.resources.wait_days_one
import cashierserviceapp.shared.generated.resources.wait_days_other
import org.jetbrains.compose.resources.stringResource

/**
 * The counted phrases every order row shares.
 *
 * They live here rather than next to each card because the same wording appears on Home, Order,
 * History and Search — one copy means "2 devices" can't drift into "2 units" on one screen. Both
 * come in a one/other pair: English inflects the noun, Indonesian doesn't, and the catalog is the
 * only place that difference belongs.
 */
@Composable
fun deviceCountLabel(count: Int): String = when (count) {
    1 -> stringResource(Res.string.device_count_one)
    else -> stringResource(Res.string.device_count_other, count)
}

/**
 * How long an order has been waiting, or "" when the timestamp didn't parse — a row with no date
 * says nothing rather than guessing.
 */
@Composable
fun waitLabel(days: Int?): String = when {
    days == null -> ""
    days <= 0 -> stringResource(Res.string.date_today)
    days == 1 -> stringResource(Res.string.wait_days_one)
    else -> stringResource(Res.string.wait_days_other, days)
}
