package com.cashierserviceapp

object URLs {
    /** Host machine as seen from the Android emulator, via its NAT alias for the host loopback. */
    const val ANDROID_LOCAL_URL = "http://192.168.1.215:8080"

    /**
     * Host machine on the LAN. Works from the emulator *and* a physical device, but only while the
     * server binds all interfaces (`SERVER_HOSTNAME=0.0.0.0`) and only on this network — re-check
     * the address with `ipconfig getifaddr en0` when you switch Wi-Fi.
     */
    const val LAN_URL = "http://192.168.1.215:8080"

    /**
     * Host machine as seen from the iOS simulator (and any host-local target).
     *
     * Literal IPv4, not `localhost`: the simulator resolves `localhost` to `::1` first, and a
     * server bound only to `127.0.0.1` refuses that connection (NSURLErrorNetworkConnectionLost -1004).
     */
    const val LOCAL_URL = "http://0.0.0.0:8080"

    /** Public test backend, used to exercise the network stack without the local server. */
    const val JSON_PLACEHOLDER_URL = "https://jsonplaceholder.typicode.com"
}