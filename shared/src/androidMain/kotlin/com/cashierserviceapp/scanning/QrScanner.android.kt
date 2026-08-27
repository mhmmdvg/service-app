package com.cashierserviceapp.scanning

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

/**
 * Google Play's own scanner.
 *
 * Worth the dependency for one reason above all: the camera is opened by Play Services, in its own
 * process, so the app declares no `CAMERA` permission and there is no runtime prompt, no preview
 * surface and no analyser to own. The trade is that it needs Play Services on the device.
 */
@Composable
actual fun rememberQrScanner(): QrScanner? {
    val context = LocalContext.current
    return remember(context) { GmsQrScanner(context) }
}

private class GmsQrScanner(private val context: Context) : QrScanner {

    override fun scan(onResult: (String) -> Unit, onError: (String?) -> Unit) {
        val options = GmsBarcodeScannerOptions.Builder()
            // Receipts carry a QR and nothing else; anything wider just invites a misread.
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom()
            .build()

        GmsBarcodeScanning.getClient(context, options)
            .startScan()
            .addOnSuccessListener { barcode -> barcode.rawValue?.let(onResult) }
            // Backing out of the scanner is a decision, not a failure — nothing to report.
            .addOnCanceledListener { }
            .addOnFailureListener { throwable -> onError(throwable.message) }
    }
}
