package com.cashierserviceapp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QueryParams(
    val search: String? = null,
    @SerialName("per_page")
    val perPage: Int? = null,
    val page: Int? = null,
)