package com.cashierserviceapp.di

import com.cashierserviceapp.domain.repositories.OrderRepository
import com.cashierserviceapp.flags.FlagsManager
import com.cashierserviceapp.storage.ApplicationStorage
import com.cashierserviceapp.utils.BufferedDelegatingLogger
import com.cashierserviceapp.utils.Logger
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import kotlinx.coroutines.CoroutineScope

interface AppGraph : ViewModelGraph {
    val flagsManager: FlagsManager
    val logger: Logger
    val applicationStorage: ApplicationStorage

    @BaseUrl
    val baseUrl: String
    val scope: CoroutineScope
    val bufferedDelegatingLogger: BufferedDelegatingLogger

}