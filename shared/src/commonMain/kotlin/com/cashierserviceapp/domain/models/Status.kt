package com.cashierserviceapp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OrderStatus {
    @SerialName("received") RECEIVED,
    @SerialName("diagnosing") DIAGNOSING,
    @SerialName("inProgress") IN_PROGRESS,
    @SerialName("completed") COMPLETED,
}

/**
 * Whether a price can be attached to a device sitting in this status.
 *
 * Nothing is priced while the device is still being looked at — the fee is only known once the
 * work is actually under way. So it opens up at [OrderStatus.IN_PROGRESS] and stays open at
 * [OrderStatus.COMPLETED], where a figure that was typed wrong still has to be correctable.
 */
val OrderStatus.acceptsPrice: Boolean
    get() = ordinal >= OrderStatus.IN_PROGRESS.ordinal
