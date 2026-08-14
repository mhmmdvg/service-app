package com.cashierserviceapp

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

fun main() = application {
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