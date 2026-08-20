package com.cashierserviceapp.data.remote.repositories

import com.cashierserviceapp.domain.models.CreateSparePartRequest
import com.cashierserviceapp.domain.models.SparePart
import com.cashierserviceapp.domain.network.SparePartApi
import com.cashierserviceapp.domain.repositories.SparePartRepository
import com.cashierserviceapp.utils.unreachable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class SparePartRepositoryImpl(
    private val api: SparePartApi
) : SparePartRepository {
    override suspend fun getSpareParts(): Result<List<SparePart>> = runCatching {
        val response = api.getSpareParts()
            ?: unreachable()

        response.data ?: throw Exception(response.message)
    }

    override suspend fun createSparePart(request: CreateSparePartRequest): Result<SparePart> =
        runCatching {
            val response = api.createSparePart(request)
                ?: unreachable()

            response.data ?: throw Exception(response.message)
        }
}
