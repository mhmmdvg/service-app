package com.cashierserviceapp.localization

import kotlin.test.Test
import kotlinx.coroutines.runBlocking

class StringCatalogIOSTest {

    @Test
    fun indonesianCatalogIsComplete() = runBlocking {
        assertIndonesianCatalogIsComplete()
    }
}
