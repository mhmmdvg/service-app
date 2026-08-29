package com.cashierserviceapp.screens.orderdetail

/**
 * Turns the receipt into the byte stream a thermal printer speaks.
 *
 * ESC/POS is the near-universal command set for 58mm and 80mm rolls. It is a *character* protocol:
 * text goes down as bytes and the printer lays it out in its own monospace font, which is why
 * [buildReceipt] has already padded every line to the column width — the printer does no wrapping
 * and no alignment of its own.
 *
 * The QR code is the exception. It can't be spelled in characters, so it goes down as a command the
 * printer's firmware draws itself (`GS ( k`), rather than as a bitmap we'd have to rasterise and
 * send a row at a time.
 */
private object EscPos {
    /** `ESC @` — reset. Clears whatever the last job left in the printer's state. */
    val Initialise = byteArrayOf(0x1B, 0x40)

    /** `ESC a n` — 0 left, 1 centre. */
    val AlignLeft = byteArrayOf(0x1B, 0x61, 0x00)
    val AlignCentre = byteArrayOf(0x1B, 0x61, 0x01)

    const val LineFeed: Byte = 0x0A

    /** Clears the tear bar, so the last line isn't still inside the printer. */
    val FeedToTear = ByteArray(4) { LineFeed }
}

/**
 * QR module size, 1–16. Eight puts the code at roughly a third of an 80mm roll — comfortably
 * scannable. Six is the floor a phone camera still reads reliably.
 */
private const val QR_MODULE_SIZE: Byte = 8

/** Error correction M — 15% recoverable, which is the usual trade for a receipt that gets pocketed. */
private const val QR_ERROR_CORRECTION: Byte = 49

/** The receipt as ESC/POS, ready to write to a printer's output stream. */
fun List<ReceiptSegment>.toEscPos(): ByteArray {
    val out = mutableListOf<Byte>()

    fun write(bytes: ByteArray) = bytes.forEach { out.add(it) }

    write(EscPos.Initialise)

    forEach { segment ->
        when (segment) {
            is ReceiptSegment.Lines -> {
                write(EscPos.AlignLeft)
                write(segment.text.toPrinterBytes())
                out.add(EscPos.LineFeed)
            }

            is ReceiptSegment.Qr -> {
                write(EscPos.AlignCentre)
                write(qrCommands(segment.data))
                write(EscPos.AlignLeft)
            }
        }
    }

    write(EscPos.FeedToTear)

    return out.toByteArray()
}

/**
 * `GS ( k` — store the payload in the symbol buffer, then print it.
 *
 * The length field counts the three bytes of the instruction itself on top of the data, which is
 * the part of this command everyone gets wrong.
 */
private fun qrCommands(data: String): ByteArray {
    val payload = data.toPrinterBytes()
    val length = payload.size + 3

    return byteArrayOf(
        // Model 2 — what every reader made this century expects.
        0x1D, 0x28, 0x6B, 0x04, 0x00, 0x31, 0x41, 0x32, 0x00,
        0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x43, QR_MODULE_SIZE,
        0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x45, QR_ERROR_CORRECTION,
        0x1D, 0x28, 0x6B, (length and 0xFF).toByte(), (length shr 8).toByte(), 0x31, 0x50, 0x30,
    ) + payload + byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30)
}

/**
 * Typographic characters the catalogs use, mapped to the ASCII the printer can spell.
 *
 * One character in, one character out, always: [buildReceipt] has already padded every line to the
 * column width, so a substitution that changed length would knock that line out of alignment.
 * That's why the ellipsis becomes a single dot rather than three.
 */
private val ASCII_SUBSTITUTES = mapOf(
    '\u2014' to '-', '\u2013' to '-', // em and en dash
    '\u2018' to '\'', '\u2019' to '\'', // curly single quotes
    '\u201C' to '"', '\u201D' to '"', // curly double quotes
    '\u2026' to '.', // ellipsis
    '\u00A0' to ' ', // non-breaking space
)

/**
 * ASCII, with anything else substituted or replaced.
 *
 * The printer decodes bytes through whichever code page it happens to be set to — never UTF-8 — so
 * a multi-byte character doesn't come out wrong, it comes out as two or three wrong characters and
 * pushes every column on that line out of line.
 *
 * ponytail: ASCII-only, so a name with a diacritic still prints a `?`. Indonesian needs none;
 * select a code page with `ESC t` and map to it if that starts happening.
 */
private fun String.toPrinterBytes(): ByteArray = ByteArray(length) { i ->
    val char = ASCII_SUBSTITUTES[this[i]] ?: this[i]
    if (char.code <= 0x7F) char.code.toByte() else '?'.code.toByte()
}
