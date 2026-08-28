package com.cashierserviceapp.printing

import androidx.compose.runtime.Composable

/** A printer the phone is already paired with. Pairing is the OS's job, never the app's. */
data class PairedPrinter(val name: String, val address: String)

/**
 * Why the printer list is or isn't usable.
 *
 * Split out because all four of these look identical from the UI otherwise — an empty list — and
 * the fix for each is somewhere different: a prompt, the system settings screen, the Bluetooth
 * toggle, or the pairing screen. A cashier told only "no printer" can't act on any of them.
 */
enum class PrinterAccess {
    /** Permission held and the radio is on. [Printing.printers] is meaningful. */
    Ready,

    /** Not asked yet, or softly denied. A prompt will still appear. */
    NeedsPermission,

    /**
     * Denied for good — Android stops showing the prompt after the second refusal and auto-denies
     * silently. Only the app's own settings page can undo it.
     */
    Blocked,

    /** Permission is held, but the radio is switched off. */
    BluetoothOff,
}

/**
 * Sends bytes to a Bluetooth receipt printer.
 *
 * Deliberately small: discovery, pairing and the printer's own settings all belong to the system
 * Bluetooth screen, which does them better than an app can and has the user's trust for it. All
 * that's left is picking one of the paired devices and writing to it.
 */
interface Printing {
    /** What, if anything, is standing between the app and the paired printers. */
    val access: PrinterAccess

    /** Paired devices. Empty unless [access] is [PrinterAccess.Ready]. */
    val printers: List<PairedPrinter>

    /**
     * Asks for what [access] is missing: the permission prompt, or — once Android has stopped
     * showing that — the app's settings page, which is the only way back from [PrinterAccess.Blocked].
     */
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
