package com.cashierserviceapp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The `page_info` block the paginated list endpoints return alongside their rows.
 *
 * [totalPages] and [hasNext] are computed server-side, so nothing here has to work out the end.
 */
@Serializable
data class PageInfo(
    val page: Int,
    @SerialName("per_page")
    val perPage: Int,
    /** Rows matching in full, not just this page — what a list's counter should show. */
    val total: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("has_next")
    val hasNext: Boolean,
    @SerialName("has_prev")
    val hasPrev: Boolean,
)
