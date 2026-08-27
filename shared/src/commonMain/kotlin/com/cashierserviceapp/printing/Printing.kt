package com.cashierserviceapp.printing

import androidx.compose.runtime.Composable

/** A printer the phone is already paired with. Pairing is the OS's job, never the app's. */
data class PairedPrinter(val name: String, val address: String)

/**
 * Sends bytes to a Bluetooth receipt printer.
 *
 * Deliberately small: discovery, pairing and the printer's own settings all belong to the system
 * Bluetooth screen, which does them better than an app can and has the user's trust for it. All
 * that's left is picking one of the paired devices and writing to it.
 */
interface Printing {
    /** Paired devices. Empty until [needsAccess] is satisfied — the OS reports nothing without it. */
    val printers: List<PairedPrinter>

    /** True while Bluetooth permission is still outstanding. */
    val needsAccess: Boolean

    /** Prompts for Bluetooth permission. No-op once granted. */
    fun requestAccess()

    /** Opens a socket, writes [bytes], closes it. Failures come back as a [Result], not a throw. */
    suspend fun print(printer: PairedPrinter, bytes: ByteArray): Result<Unit>
}

/**
 * The platform's printer access, or null where there is none — which is the honest answer on iOS
 * and desktop, and lets the UI simply not offer a Print button rather than offer one that fails.
 *
 * iOS is null by policy, not by omission: receipt printers in this class speak Classic Bluetooth
 * SPP, and iOS only opens that to MFi-certified hardware. A non-MFi printer is unreachable from an
 * iPhone however much code is thrown at it.
 */
@Composable
expect fun rememberPrinting(): Printing?
