package com.cashierserviceapp

import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import com.cashierserviceapp.di.IosAppGraph
import com.cashierserviceapp.flags.Flags
import com.cashierserviceapp.utils.Logger
import dev.zacsweers.metro.createGraphFactory
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
private val appGraph = createGraphFactory<IosAppGraph.Factory>()
    .create(
        Flags(
            enableBackOnTopLevelScreens = false,
            rippleEnabled = false,
            hideKeyboardOnDrag = true
        )
    )

@Suppress("unused")
fun MainViewController() = ComposeUIViewController(
    configure = { onFocusBehavior = OnFocusBehavior.DoNothing }
) { App(appGraph) }