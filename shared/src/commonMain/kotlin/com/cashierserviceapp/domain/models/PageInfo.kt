package com.cashierserviceapp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The `page_info` block every paginated list endpoint returns alongside its rows.
 *
 * [totalPages] and [hasNext] are both computed server-side from [total] and [perPage], so a client
 * never has to work out where the end is — see `PageInfo` in the Vapor project.
 */
@Serializable
data class PageInfo(
    val page: Int,
    @SerialName("per_page")
    val perPage: Int,
    /** Rows matching the query in full, not just on this page — what a list's counter should show. */
    val total: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("has_next")
    val hasNext: Boolean,
    @SerialName("has_prev")
    val hasPrev: Boolean,
)
