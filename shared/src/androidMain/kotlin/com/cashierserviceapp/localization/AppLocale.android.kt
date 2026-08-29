package com.cashierserviceapp.localization

import java.util.Locale

/**
 * Deliberately not `AppCompatDelegate.setApplicationLocales`: below API 33 that path applies the
 * locale by recreating live `AppCompatActivity` instances, and this app's activity is a plain
 * `ComponentActivity`. Setting the default locale reaches Compose either way — its Android locale
 * delegate reads `android.os.LocaleList.getDefault()`, which re-derives from this — and it costs no
 * activity recreation, so the screen keeps its state.
 *
 * Called on every composition, so it no-ops when the locale is already the chosen one. Compared by
 * language rather than tag: `Locale` still spells Indonesian with the legacy code `in`, on both
 * sides of this comparison, and the region is worth keeping when it's already right.
 */
actual fun applyAppLanguage(language: AppLanguage) {
    val target = Locale.forLanguageTag(language.tag)
    if (Locale.getDefault().language != target.language) Locale.setDefault(target)
}
