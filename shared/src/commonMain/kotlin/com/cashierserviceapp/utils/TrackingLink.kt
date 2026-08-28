package com.cashierserviceapp.utils

import com.cashierserviceapp.URLs

/**
 * What a receipt's QR code encodes: a link, not a bare token.
 *
 * A customer scans the receipt with whatever camera app their phone came with, which knows what to
 * do with a URL and nothing at all with a UUID. The token alone stays useful to the people who have
 * the service app — see [qrTokenFrom], which takes the link back apart.
 */
fun trackingLink(qrToken: String): String = "${URLs.TRACKING_URL}/$qrToken"

/**
 * The token out of whatever was scanned or typed.
 *
 * Accepts both halves of the same code: the tracking link a phone camera reads off the receipt, and
 * a bare token entered by hand or read from an older receipt. Anything that isn't a URL is passed
 * through untouched, so this can sit in front of every lookup rather than only the scanning one.
 */
fun qrTokenFrom(scanned: String): String {
    val trimmed = scanned.trim()

    if (!trimmed.startsWith("http://", ignoreCase = true) &&
        !trimmed.startsWith("https://", ignoreCase = true)
    ) {
        return trimmed
    }

    return trimmed
        .substringBefore('?')
        .substringBefore('#')
        .trimEnd('/')
        .substringAfterLast('/')
}
