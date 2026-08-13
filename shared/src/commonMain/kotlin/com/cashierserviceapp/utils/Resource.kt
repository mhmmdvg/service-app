package com.cashierserviceapp.utils

sealed class Resource<T : Any>(val data: T? = null, val message: String? = null) {
    class Success<T : Any>(data: T?) : Resource<T>(data)
    class Error<T : Any>(message: String? = null, data: T? = null) : Resource<T>(data, message)
    class Loading<T : Any>(data: T? = null) : Resource<T>(data)
}