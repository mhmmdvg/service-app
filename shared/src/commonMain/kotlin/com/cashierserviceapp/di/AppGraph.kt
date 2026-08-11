package com.cashierserviceapp.di

import com.cashierserviceapp.flags.FlagsManager
import com.cashierserviceapp.utils.BufferedDelegatingLogger
import com.cashierserviceapp.utils.Logger
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import kotlinx.coroutines.CoroutineScope

interface AppGraph : ViewModelGraph {
    val flagsManager: FlagsManager
    val logger: Logger
    val scope: CoroutineScope
    val bufferedDelegatingLogger: BufferedDelegatingLogger
}