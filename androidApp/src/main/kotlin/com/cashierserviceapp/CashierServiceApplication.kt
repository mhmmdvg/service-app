package com.cashierserviceapp

import android.app.Application
import android.content.res.Configuration
import com.cashierserviceapp.localization.applyAppLanguage
import com.cashierserviceapp.utils.AndroidLogger
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

class CashierServiceApplication : Application(), MetroApplication {
    private val appGraph: AndroidAppGraph by lazy {
        createGraphFactory<AndroidAppGraph.Factory>().create(
            application = this
        )
    }

    override val appComponentProviders: MetroAppComponentProviders
        get() = appGraph

    override fun onCreate() {
        super.onCreate()

        initApp(
            appGraph = appGraph,
            platformLogger = AndroidLogger()
        )
    }

    /**
     * Puts the chosen language back after the system has overwritten it.
     *
     * The app moves the process locale to render in the language picked in Settings, but that
     * locale is the system's own state as much as ours: on every configuration update Android
     * re-derives it from the device's configuration, and on older releases that happens often
     * enough — resume, keyboard, theme, rotation — to look like the picker simply doesn't work.
     * This runs after that overwrite and before the recomposition that reads it.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        applyAppLanguage(appGraph.applicationStorage.getLanguageBlocking())
    }
}