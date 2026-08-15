package com.cashierserviceapp.localization

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Points the platform's locale at [language].
 *
 * This is the only lever there is. Compose Resources resolves `values-<tag>` through
 * `Locale.current`, and its `ComposeEnvironment` — the seam that decides which catalog a
 * `stringResource` reads — is `internal` to the library, so the app cannot hand it a language
 * directly. What it *can* do is move the locale that `Locale.current` reports, which every platform
 * happens to expose:
 *
 * - Android reads `android.os.LocaleList.getDefault()`, which re-derives from [java.util.Locale]
 * - Desktop reads `java.util.Locale.getDefault()`
 * - iOS reads `NSLocale.preferredLanguages`, backed by `AppleLanguages` in `NSUserDefaults`
 *
 * All three take effect in-process, so no platform needs a restart to change language.
 */
expect fun applyAppLanguage(language: AppLanguage)

/**
 * The language the app is currently rendering in.
 *
 * Static on purpose, and the reason the picker feels instant: changing a static composition local
 * recomposes the whole tree beneath the provider, so every `stringResource` re-reads
 * `Locale.current` and resolves against the new catalog. Nothing else would invalidate them —
 * [applyAppLanguage] mutates process-global state that Compose has no way to observe.
 *
 * Recomposition is not re-creation: `remember`ed state, the navigation back stack included, is
 * untouched.
 */
val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.EN }
