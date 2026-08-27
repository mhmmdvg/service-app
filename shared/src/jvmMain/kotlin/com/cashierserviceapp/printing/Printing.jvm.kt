package com.cashierserviceapp.printing

import androidx.compose.runtime.Composable

/** Desktop has no Bluetooth stack on the JVM worth the name; receipts print from a phone. */
@Composable
actual fun rememberPrinting(): Printing? = null
