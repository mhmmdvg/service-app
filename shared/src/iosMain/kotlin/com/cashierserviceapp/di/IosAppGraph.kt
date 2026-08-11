package com.cashierserviceapp.di

import com.cashierserviceapp.flags.Flags
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(AppScope::class)
interface IosAppGraph : AppGraph {

    @DependencyGraph.Factory
    interface Factory {
        fun create(
            @Provides platformFlags: Flags
        ): IosAppGraph
    }
}