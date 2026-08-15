package com.cashierserviceapp.localization

import platform.Foundation.NSUserDefaults

/**
 * `NSLocale.preferredLanguages` — what Compose reads for `Locale.current` on iOS — is backed by
 * this defaults key, so writing it moves the language for the running process rather than only for
 * the next launch. It persists too, which is the wanted side effect: system-drawn UI comes up in
 * the chosen language from the next cold start on.
 */
private const val APPLE_LANGUAGES_KEY = "AppleLanguages"

actual fun applyAppLanguage(language: AppLanguage) {
    NSUserDefaults.standardUserDefaults.setObject(listOf(language.tag), APPLE_LANGUAGES_KEY)
}
