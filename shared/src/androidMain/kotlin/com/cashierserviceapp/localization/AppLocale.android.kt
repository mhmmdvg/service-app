package com.cashierserviceapp.localization

import java.util.Locale

/**
 * Deliberately not `AppCompatDelegate.setApplicationLocales`: below API 33 that path applies the
 * locale by recreating live `AppCompatActivity` instances, and this app's activity is a plain
 * `ComponentActivity`. Setting the default locale reaches Compose either way — its Android locale
 * delegate reads `android.os.LocaleList.getDefault()`, which re-derives from this — and it costs no
 * activity recreation, so the screen keeps its state.
 */
actual fun applyAppLanguage(language: AppLanguage) {
    Locale.setDefault(Locale.forLanguageTag(language.tag))
}
