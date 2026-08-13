package com.cashierserviceapp

import com.cashierserviceapp.di.AppGraph
import com.cashierserviceapp.flags.FlagsManager
import com.cashierserviceapp.utils.BufferedDelegatingLogger
import com.cashierserviceapp.utils.DebugLogger
import com.cashierserviceapp.utils.Logger
import com.cashierserviceapp.utils.NoopProdLogger
import io.ktor.utils.io.ReaderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun initApp(
    appGraph: AppGraph,
    platformLogger: Logger,
) {
    initFlagsAndLogging(
        appScope = appGraph.scope,
        platformLogger = platformLogger,
        bufferedDelegatingLogger = appGraph.bufferedDelegatingLogger,
        flagsManager = appGraph.flagsManager
    )
}

private fun initFlagsAndLogging(
    appScope: CoroutineScope,
    platformLogger: Logger,
    bufferedDelegatingLogger: BufferedDelegatingLogger,
    flagsManager: FlagsManager
) {
    appScope.launch {
        val flags = flagsManager.initAndGetFlags()
        bufferedDelegatingLogger.attach(
            when {
                flags.debugLogging -> DebugLogger(platformLogger)
                else -> NoopProdLogger()
            }
        )
    }
}