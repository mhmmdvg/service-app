package com.cashierserviceapp.scanning

import androidx.compose.runtime.Composable

/**
 * Reads the QR code printed on a receipt.
 *
 * Only ever hands back the decoded string, which is the same thing the token field takes by hand —
 * so the lookup, and everything downstream of it, has one code path whether the token was scanned
 * or typed.
 */
interface QrScanner {
    /**
     * Opens the scanner. [onResult] fires with the decoded text; a cancelled scan fires nothing,
     * because backing out is not an error worth reporting.
     */
    fun scan(onResult: (String) -> Unit, onError: (String?) -> Unit)
}

/**
 * The platform's scanner, or null where there is none — iOS and desktop keep the typed field, which
 * has always been the fallback for a cracked camera or a smudged roll anyway.
 */
@Composable
expect fun rememberQrScanner(): QrScanner?
