package com.cashierserviceapp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Status {
    @SerialName("received") RECEIVED,
    @SerialName("diagnosing") DIAGNOSING,
    @SerialName("inProgress") IN_PROGRESS,
    @SerialName("completed") COMPLETED,

}