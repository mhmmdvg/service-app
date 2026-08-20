package com.cashierserviceapp.utils

import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.error_unreachable
import org.jetbrains.compose.resources.getString

/**
 * Nothing came back at all: no envelope to read a reason out of, so the network is the suspect.
 *
 * Suspending because the wording comes out of the string catalog. Repositories run outside
 * composition, so [getString] is how they reach the same `values-<tag>` catalog a `stringResource`
 * would — and this message goes straight onto the screen, so it has to follow the app's language
 * like every other line of copy.
 */
suspend fun unreachable(): Nothing = throw Exception(getString(Res.string.error_unreachable))
