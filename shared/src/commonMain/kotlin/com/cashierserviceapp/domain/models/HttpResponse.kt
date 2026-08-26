package com.cashierserviceapp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HttpResponse<T : Any>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    /** Only the paginated list endpoints send this; null everywhere else. */
    @SerialName("page_info")
    val pageInfo: PageInfo? = null,
)
