package com.cashierserviceapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform