package com.cashierserviceapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cashierserviceapp.di.AppGraph
import com.cashierserviceapp.flags.LocalFlags
import com.cashierserviceapp.localization.LocalAppLanguage
import com.cashierserviceapp.localization.applyAppLanguage
import com.cashierserviceapp.navigation.NavHost
import com.cashierserviceapp.utils.LocalWindowSize
import com.cashierserviceapp.utils.windowSize
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory

@Composable
fun App(
    appGraph: AppGraph,
    onThemeChange: ((isDarkTheme: Boolean) -> Unit)? = null
) {
    val storage = appGraph.applicationStorage

    // Seeded from the persisted value so the very first frame is already in the chosen theme and
    // language — no flash of the wrong one while a flow warms up.
    val currentTheme by storage.getTheme()
        .collectAsStateWithLifecycle(initialValue = remember { storage.getThemeBlocking() })
    val language by storage.getLanguage()
        .collectAsStateWithLifecycle(initialValue = remember { storage.getLanguageBlocking() })

    // Pushed to the platform locale during composition rather than from a LaunchedEffect: Compose
    // Resources reads the locale as it composes, so an effect would land a frame late and the app
    // would flash the previous language on every change, including the very first frame.
    remember(language) { applyAppLanguage(language) }

    val isDarkTheme = when (currentTheme) {
        Theme.SYSTEM -> isSystemInDarkTheme()
        Theme.LIGHT -> false
        Theme.DARK -> true
    }

    val flags by appGraph.flagsManager.flags.collectAsStateWithLifecycle()

    // Read once, synchronously. The session StateFlow is seeded from multiplatform-settings at
    // construction, so `.value` is already correct here — no loading state, no wrong-screen flash.
    // Deliberately not collected: after this first frame, navigation is driven by explicit
    // login/logout calls, not by the session flow.
    val isLoggedIn = remember { appGraph.applicationStorage.session.value != null }

    CompositionLocalProvider(
        LocalFlags provides flags,
        LocalAppGraph provides appGraph,
        LocalMetroViewModelFactory provides appGraph.metroViewModelFactory,
        LocalWindowSize provides windowSize(),
        LocalAppLanguage provides language,
    ) {
        NavHost(
            isOnboardingComplete = true,
            isLoggedIn = isLoggedIn,
            isDarkTheme = isDarkTheme,
            onThemeChange = onThemeChange
        )
    }
}

public val LocalAppGraph: ProvidableCompositionLocal<AppGraph> =
    staticCompositionLocalOf {
        error("No AppGraph registered")
    }