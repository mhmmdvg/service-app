package com.cashierserviceapp.utils

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The printed QR and the scanner are two halves of one contract: whatever [trackingLink] puts on the
 * roll, [qrTokenFrom] has to hand back. A receipt is printed once and read months later, so these
 * two drifting apart is not something a later release can fix.
 */
class TrackingLinkTest {

    private val token = "A0BF80BD-9C58-4BB7-82A0-440AFD240471"

    @Test
    fun theScannerUndoesWhatThePrinterDid() {
        assertEquals(token, qrTokenFrom(trackingLink(token)))
    }

    @Test
    fun theLinkKeepsTheTrackSegment() {
        // Without it the site serves its own 404 rather than the order.
        assertEquals(
            "https://service-tracking-eight.vercel.app/track/$token",
            trackingLink(token)
        )
    }

    @Test
    fun aBareTokenIsPassedThrough() {
        // Receipts printed before the QR carried a link, and anything typed into the token field.
        assertEquals(token, qrTokenFrom(token))
        assertEquals(token, qrTokenFrom("  $token \n"))
    }

    @Test
    fun stripsWhatACameraOrAShareSheetMayAppend() {
        assertEquals(token, qrTokenFrom("https://service-tracking-eight.vercel.app/track/$token/"))
        assertEquals(token, qrTokenFrom("https://service-tracking-eight.vercel.app/track/$token?utm=qr"))
        assertEquals(token, qrTokenFrom("https://service-tracking-eight.vercel.app/track/$token#top"))
        assertEquals(token, qrTokenFrom("HTTPS://service-tracking-eight.vercel.app/track/$token"))
    }
}
