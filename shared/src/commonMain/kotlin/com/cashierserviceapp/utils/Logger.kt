package com.cashierserviceapp.utils

interface Logger {
    fun log(tag: String, lazyMessage: () -> String)
}

fun Logger.tagged(tag: String) = TaggedLogger(tag, this)

class TaggedLogger(
    private val tag: String,
    private val delegate: Logger
) {
    fun log(lazyMessage: () -> String) {
        delegate.log(tag, lazyMessage)
    }
}

class NoopProdLogger : Logger {
    override fun log(tag: String, lazyMessage: () -> String) = Unit
}

interface LogExporter {
    fun getAllLogs(): String?
}

internal const val MAX_LOG_MESSAGE_IN_MEMORY = 200