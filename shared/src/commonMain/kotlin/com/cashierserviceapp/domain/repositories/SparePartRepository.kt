package com.cashierserviceapp.domain.repositories

import com.cashierserviceapp.domain.models.CreateSparePartRequest
import com.cashierserviceapp.domain.models.SparePart

interface SparePartRepository {
    suspend fun getSpareParts(): Result<List<SparePart>>

    suspend fun createSparePart(request: CreateSparePartRequest): Result<SparePart>
}
