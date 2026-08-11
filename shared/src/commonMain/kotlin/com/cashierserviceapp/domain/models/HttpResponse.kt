package com.cashierserviceapp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class HttpResponse<T : Any>(
    val status: Boolean,
    val message: String,
    val data: T? = null,
)
