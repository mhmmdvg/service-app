package com.cashierserviceapp

import androidx.compose.ui.ComposeUiFlags
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.isDialogAnimationEnabled
import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import com.cashierserviceapp.di.IosAppGraph
import com.cashierserviceapp.flags.Flags
import com.cashierserviceapp.utils.Logger
import dev.zacsweers.metro.createGraphFactory
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform
import platform.Foundation.NSLog

@Suppress("unused")
fun initApp() = initApp(
    appGraph = appGraph,
    platformLogger = IOSLogger(),
)

class IOSLogger : Logger {
    override fun log(tag: String, lazyMessage: () -> String) {
        NSLog("[$tag] ${lazyMessage()}")
    }
}
@OptIn(ExperimentalNativeApi::class)
private val appGraph = createGraphFactory<IosAppGraph.Factory>()
    .create(
        Flags(
            enableBackOnTopLevelScreens = false,
            rippleEnabled = false,
            hideKeyboardOnDrag = true,
            debugLogging = Platform.isDebugBinary
        )
    )

@Suppress("unused")
@OptIn(ExperimentalComposeUiApi::class)
fun MainViewController() = ComposeUIViewController(
    configure = {
        onFocusBehavior = OnFocusBehavior.DoNothing
        // Skiko targets fade and scale every dialog into place on their own, which Android does
        // not. The sheets bring their own entrance, and the two read as one muddled move together.
        ComposeUiFlags.isDialogAnimationEnabled = false
    }
) { App(appGraph) }