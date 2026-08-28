package com.cashierserviceapp

object URLs {
    /**
     * The deployed backend — what the app talks to unless someone is working on the server.
     *
     * HTTPS, so it needs none of the cleartext allowance the local addresses below do; that stays
     * in the manifest only for them.
     */
    const val PRODUCTION_URL = "https://cashier-api.up.railway.app"

    /**
     * The public tracking page a printed QR code points at, without its trailing token.
     *
     * Includes the `/track` segment: the site 404s on a bare token at the root, so a receipt
     * printed against the shorter form would send every customer to a Not Found page.
     */
    const val TRACKING_URL = "https://service-tracking-eight.vercel.app/track"

    /** Host machine as seen from the Android emulator, via its NAT alias for the host loopback. */
    const val ANDROID_LOCAL_URL = "http://192.168.1.226:8080"

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