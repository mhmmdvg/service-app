package com.cashierserviceapp.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.LocalSystemTheme
import androidx.compose.ui.SystemTheme

@OptIn(InternalComposeUiApi::class)
@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual object LocalAppTheme {
    private var default: SystemTheme? = null
    actual val current: Boolean
        @Composable get() = TODO("Not yet implemented")

    @Composable
    actual infix fun provides(value: Boolean?): ProvidedValue<*> {
        if (default != null) {
            default = LocalSystemTheme.current
        }
        val new = when (value) {
            true -> SystemTheme.Dark
            false -> SystemTheme.Light
            null -> default!!
        }

        return LocalSystemTheme.provides(new)
    }
}