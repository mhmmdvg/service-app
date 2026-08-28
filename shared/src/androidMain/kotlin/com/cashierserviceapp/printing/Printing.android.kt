package com.cashierserviceapp.printing

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

/**
 * The Serial Port Profile UUID. Thermal printers present themselves as a serial port and take a
 * plain byte stream — there is no printer-specific profile involved.
 */
private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

/**
 * Only Android 12 asks at runtime. Below that the install-time `BLUETOOTH` permission in the
 * manifest is the whole story, so there is nothing to prompt for.
 */
private val RUNTIME_PERMISSION_REQUIRED = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@Composable
actual fun rememberPrinting(): Printing? {
    val context = LocalContext.current
    val activity = LocalActivity.current

    var granted by remember(context) { mutableStateOf(context.hasBluetoothAccess()) }

    /**
     * Android stops prompting after the second refusal and denies instantly instead, which is
     * indistinguishable from the prompt never having been shown. The tell is that the result comes
     * back denied *and* the system no longer wants a rationale shown — checked here rather than
     * before launching, where it also reads false on the very first ask.
     */
    var blocked by remember(context) { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        granted = isGranted
        blocked = !isGranted && activity?.shouldShowRequestPermissionRationale(
            Manifest.permission.BLUETOOTH_CONNECT
        ) == false
    }

    // Keyed on the flags so the paired list is re-read the moment permission arrives, rather than
    // staying empty until the sheet is reopened.
    return remember(context, granted, blocked) {
        AndroidPrinting(
            context = context,
            granted = granted,
            blocked = blocked,
            requestPermission = { launcher.launch(Manifest.permission.BLUETOOTH_CONNECT) },
        )
    }
}

private class AndroidPrinting(
    private val context: Context,
    private val granted: Boolean,
    private val blocked: Boolean,
    private val requestPermission: () -> Unit,
) : Printing {

    private val adapter: BluetoothAdapter?
        get() = context.getSystemService(BluetoothManager::class.java)?.adapter

    override val access: PrinterAccess
        get() = when {
            blocked -> PrinterAccess.Blocked
            !granted -> PrinterAccess.NeedsPermission
            // A phone with no radio at all reads the same as one switched off: nothing to print to.
            adapter?.isEnabled != true -> PrinterAccess.BluetoothOff
            else -> PrinterAccess.Ready
        }

    /**
     * Every bonded device, not just the ones that claim to be printers. Cheap thermal printers
     * routinely report the wrong Bluetooth device class, so filtering on it hides the very hardware
     * this screen exists for.
     */
    override val printers: List<PairedPrinter>
        get() {
            if (access != PrinterAccess.Ready) return emptyList()

            return runCatching {
                adapter?.bondedDevices.orEmpty()
                    .map { PairedPrinter(it.name ?: it.address, it.address) }
            }.getOrDefault(emptyList())
        }

    override fun requestAccess() {
        when (access) {
            PrinterAccess.NeedsPermission -> requestPermission()
            // The prompt is gone for good; the settings page is the only way to turn it back on.
            PrinterAccess.Blocked -> context.openAppSettings()
            PrinterAccess.BluetoothOff -> context.openBluetoothSettings()
            PrinterAccess.Ready -> Unit
        }
    }

    override suspend fun print(printer: PairedPrinter, bytes: ByteArray): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val adapter = adapter ?: error("Bluetooth is unavailable on this device")
                val device = adapter.getRemoteDevice(printer.address)

                // No cancelDiscovery() here on purpose: it needs BLUETOOTH_SCAN, which this app has
                // no other use for, and asking for a second permission to cancel a scan we never
                // start only bought a SecurityException on every print.
                device.createRfcommSocketToServiceRecord(SPP_UUID).use { socket ->
                    socket.connect()
                    socket.outputStream.apply {
                        write(bytes)
                        flush()
                    }
                    // The socket closes the moment `use` returns; without a beat the printer can
                    // lose the tail of the buffer it hasn't rendered yet.
                    Thread.sleep(PRINT_DRAIN_MILLIS)
                }
            }.recoverCatching { throwable ->
                throw when (throwable) {
                    // Only blame permissions when one is actually missing. A SecurityException
                    // from a call needing a permission never asked for reads identically, and
                    // reporting that as "withdrawn" sent a real diagnosis down the wrong path.
                    is SecurityException -> IOException(
                        if (access == PrinterAccess.Ready) {
                            "Bluetooth refused the request: ${throwable.message}"
                        } else {
                            "Bluetooth permission is missing"
                        }
                    )
                    is IOException -> IOException(
                        "Couldn't reach ${printer.name}. Check it is on, in range and not " +
                                "connected to another phone.",
                        throwable
                    )

                    else -> throwable
                }
            }
        }

    private companion object {
        const val PRINT_DRAIN_MILLIS = 400L
    }
}

private fun Context.openAppSettings() = startActivity(
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
        // Started from a Context that may not be an Activity's, so it needs its own task.
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
)

private fun Context.openBluetoothSettings() = startActivity(
    Intent(Settings.ACTION_BLUETOOTH_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
)

private fun Context.hasBluetoothAccess(): Boolean =
    !RUNTIME_PERMISSION_REQUIRED || ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.BLUETOOTH_CONNECT
    ) == PackageManager.PERMISSION_GRANTED
