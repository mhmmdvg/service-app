package com.cashierserviceapp.domain.models

data class Validation(
    val success: Boolean,
    val message: String? = null,
)