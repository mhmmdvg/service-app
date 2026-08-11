package com.cashierserviceapp

import android.app.Application
import com.cashierserviceapp.di.BaseAndroidAppGraph
import com.cashierserviceapp.flags.Flags
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(AppScope::class)
interface AndroidAppGraph : BaseAndroidAppGraph {
    @DependencyGraph.Factory
    interface Factory {
        fun create(
            @Provides application: Application,
            @Provides platformFlags: Flags = Flags()
        ): AndroidAppGraph
    }
}