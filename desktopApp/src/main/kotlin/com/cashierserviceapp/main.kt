package com.cashierserviceapp

import androidx.compose.ui.ComposeUiFlags
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.isDialogAnimationEnabled
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.cashierserviceapp.di.JvmAppGraph
import com.cashierserviceapp.flags.Flags
import com.cashierserviceapp.utils.Logger
import dev.zacsweers.metro.createGraphFactory

class JvmLogger : Logger {
    override fun log(tag: String, lazyMessage: () -> String) {
        println("[$tag] ${lazyMessage()}")
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Skiko targets fade and scale every dialog into place on their own, which Android does not.
    // The sheets bring their own entrance, and the two read as one muddled move together. Set
    // before the composition starts rather than inside it, so it isn't a side effect of a frame.
    ComposeUiFlags.isDialogAnimationEnabled = false

    application {
        val graph = createGraphFactory<JvmAppGraph.Factory>()
            .create(
                platformFlags = Flags(supportNotifications = false, debugLogging = true),
            )

        initApp(
            appGraph = graph,
            platformLogger = JvmLogger()
        )

        System.setProperty("apple.awt.application.appereance", "system")

        Window(
            onCloseRequest = ::exitApplication,
            title = "Cashier Service App",
            state = rememberWindowState(width = 600.dp, height = 800.dp)
        ) {
            App(graph)
        }
    }
}
