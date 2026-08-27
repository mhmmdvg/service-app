package com.cashierserviceapp.screens.orderdetail

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The byte stream is the one part of printing that can be checked without a printer, so it is
 * checked properly. A wrong length field in `GS ( k` doesn't throw — the printer prints garbage.
 */
class EscPosTest {

    @Test
    fun opensWithAResetAndEndsClearOfTheTearBar() {
        val bytes = listOf(ReceiptSegment.Lines("HELLO")).toEscPos()

        assertContentEquals(byteArrayOf(0x1B, 0x40), bytes.take(2).toByteArray())
        assertContentEquals(ByteArray(4) { 0x0A }, bytes.takeLast(4).toByteArray())
    }

    @Test
    fun writesLinesLeftAlignedAndNewlineTerminated() {
        val bytes = listOf(ReceiptSegment.Lines("AB")).toEscPos()

        assertContentEquals(
            byteArrayOf(0x1B, 0x40) +           // reset
                byteArrayOf(0x1B, 0x61, 0x00) + // align left
                byteArrayOf(0x41, 0x42, 0x0A) + // "AB\n"
                ByteArray(4) { 0x0A },
            bytes
        )
    }

    @Test
    fun countsTheInstructionBytesInTheQrLengthField() {
        val token = "322F9819-579B-4DB7-8328-6530C5F386BF" // 36 chars
        val bytes = listOf(ReceiptSegment.Qr(token)).toEscPos()

        // The store command is `GS ( k pL pH 31 50 30 <data>`; pL/pH count the three bytes 31 50 30
        // on top of the payload. Getting this wrong is the classic ESC/POS QR bug.
        val store = byteArrayOf(0x1D, 0x28, 0x6B)
        val storeAt = bytes.toList().windowedIndexOf(
            (store + byteArrayOf((token.length + 3).toByte(), 0x00, 0x31, 0x50, 0x30)).toList()
        )
        assertTrue(storeAt >= 0, "no GS ( k store command with a length of ${token.length + 3}")

        // The token follows the instruction verbatim, and the print command closes it out.
        val dataAt = storeAt + 8
        assertEquals(token, bytes.copyOfRange(dataAt, dataAt + token.length).decodeToString())
        assertContentEquals(
            byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30),
            bytes.copyOfRange(dataAt + token.length, dataAt + token.length + 8)
        )
    }

    @Test
    fun centresTheQrAndPutsAlignmentBack() {
        val bytes = listOf(ReceiptSegment.Qr("X")).toEscPos().toList()

        val centre = bytes.windowedIndexOf(listOf<Byte>(0x1B, 0x61, 0x01))
        val left = bytes.windowedIndexOf(listOf<Byte>(0x1B, 0x61, 0x00))
        assertTrue(centre >= 0 && left > centre, "QR must be centred, then alignment restored")
    }

    @Test
    fun spellsTypographicCharactersInAscii() {
        // The catalogs are written with real typography; receipt_footer's em dash is the live case.
        val bytes = listOf(ReceiptSegment.Lines("Thank you \u2014 don\u2019t lose it\u2026")).toEscPos()

        assertTrue(bytes.decodeToString().contains("Thank you - don't lose it."))
    }

    @Test
    fun substitutesOneCharacterForOneCharacter() {
        // buildReceipt has already padded each line to the column width, so a substitution that
        // changed length would shift every column after it.
        val line = "\u2014\u2019\u201C\u2026\u00A0caf\u00E9"
        val bytes = listOf(ReceiptSegment.Lines(line)).toEscPos()

        // Two bytes of reset, then three of alignment, then the text.
        val written = bytes.copyOfRange(5, 5 + line.length).decodeToString()

        assertEquals(line.length, written.length)
        assertEquals("-'\". caf?", written)
    }

    private fun <T> List<T>.windowedIndexOf(sub: List<T>): Int =
        indices.firstOrNull { i -> i + sub.size <= size && subList(i, i + sub.size) == sub } ?: -1
}
