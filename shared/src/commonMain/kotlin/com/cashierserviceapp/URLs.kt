package com.cashierserviceapp

object URLs {
    /** Host machine as seen from the Android emulator. */
    const val ANDROID_LOCAL_URL = "http://10.0.2.2:8080"

    /**
     * Host machine as seen from the iOS simulator (and any host-local target).
     *
     * Literal IPv4, not `localhost`: the simulator resolves `localhost` to `::1` first, and a
     * server bound only to `127.0.0.1` refuses that connection (NSURLErrorNetworkConnectionLost -1004).
     */
    const val LOCAL_URL = "http://127.0.0.1:8080"

    /** Public test backend, used to exercise the network stack without the local server. */
    const val JSON_PLACEHOLDER_URL = "https://jsonplaceholder.typicode.com"
}