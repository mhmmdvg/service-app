package com.cashierserviceapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

internal expect fun getPlatformId(): String