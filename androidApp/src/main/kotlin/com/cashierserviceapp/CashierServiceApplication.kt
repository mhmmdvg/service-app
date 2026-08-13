package com.cashierserviceapp

import android.app.Application
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
}