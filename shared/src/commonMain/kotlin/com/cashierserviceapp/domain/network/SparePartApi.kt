package com.cashierserviceapp.domain.network

import com.cashierserviceapp.domain.models.CreateSparePartRequest
import com.cashierserviceapp.domain.models.HttpResponse
import com.cashierserviceapp.domain.models.SparePart

interface SparePartApi {
    suspend fun getSpareParts(): HttpResponse<List<SparePart>>?

    suspend fun createSparePart(request: CreateSparePartRequest): HttpResponse<SparePart>?
}
