package com.cashierserviceapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cashierserviceapp.di.AppGraph
import com.cashierserviceapp.flags.LocalFlags
import com.cashierserviceapp.navigation.NavHost
import com.cashierserviceapp.utils.LocalWindowSize
import com.cashierserviceapp.utils.windowSize
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory

@Composable
fun App(
    appGraph: AppGraph,
    onThemeChange: ((isDarkTheme: Boolean) -> Unit)? = null
) {
    val currentTheme = Theme.SYSTEM
    val isDarkTheme = when (currentTheme) {
        Theme.SYSTEM -> isSystemInDarkTheme()
        Theme.LIGHT -> false
        Theme.DARK -> true
    }

    val flags by appGraph.flagsManager.flags.collectAsStateWithLifecycle()

    CompositionLocalProvider(
        LocalFlags provides flags,
        LocalAppGraph provides appGraph,
        LocalMetroViewModelFactory provides appGraph.metroViewModelFactory,
        LocalWindowSize provides windowSize(),
    ) {
        NavHost(true, isDarkTheme = isDarkTheme, onThemeChange = onThemeChange)
    }
}

public val LocalAppGraph: ProvidableCompositionLocal<AppGraph> =
    staticCompositionLocalOf {
        error("No AppGraph registered")
    }