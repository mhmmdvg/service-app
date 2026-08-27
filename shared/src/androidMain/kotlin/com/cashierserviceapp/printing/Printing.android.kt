package com.cashierserviceapp.printing

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
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

    var granted by remember(context) { mutableStateOf(context.hasBluetoothAccess()) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted = it }

    // Keyed on `granted` so the paired list is re-read the moment permission arrives, rather than
    // staying empty until the sheet is reopened.
    return remember(context, granted) {
        AndroidPrinting(
            context = context,
            granted = granted,
            requestPermission = { launcher.launch(Manifest.permission.BLUETOOTH_CONNECT) },
        )
    }
}

private class AndroidPrinting(
    private val context: Context,
    private val granted: Boolean,
    private val requestPermission: () -> Unit,
) : Printing {

    private val adapter: BluetoothAdapter?
        get() = context.getSystemService(BluetoothManager::class.java)?.adapter

    override val needsAccess: Boolean get() = !granted

    /**
     * Every bonded device, not just the ones that claim to be printers. Cheap thermal printers
     * routinely report the wrong Bluetooth device class, so filtering on it hides the very hardware
     * this screen exists for.
     */
    override val printers: List<PairedPrinter>
        get() {
            if (!granted) return emptyList()

            // Bonded devices are readable without the radio being on, but connecting isn't, so an
            // adapter that is off is the same as having nothing to offer.
            val adapter = adapter?.takeIf { it.isEnabled } ?: return emptyList()

            return runCatching {
                adapter.bondedDevices.map { PairedPrinter(it.name ?: it.address, it.address) }
            }.getOrDefault(emptyList())
        }

    override fun requestAccess() {
        if (granted) return
        requestPermission()
    }

    override suspend fun print(printer: PairedPrinter, bytes: ByteArray): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val adapter = adapter ?: error("Bluetooth is unavailable on this device")
                val device = adapter.getRemoteDevice(printer.address)

                // Discovery and a connection attempt contend for the same radio, and discovery wins
                // — which shows up as a connect that times out for no visible reason.
                adapter.cancelDiscovery()

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
                    is SecurityException -> IOException("Bluetooth permission was withdrawn")
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

private fun Context.hasBluetoothAccess(): Boolean =
    !RUNTIME_PERMISSION_REQUIRED || ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.BLUETOOTH_CONNECT
    ) == PackageManager.PERMISSION_GRANTED
