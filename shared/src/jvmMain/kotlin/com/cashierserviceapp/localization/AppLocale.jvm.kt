package com.cashierserviceapp.localization

import java.util.Locale

actual fun applyAppLanguage(language: AppLanguage) {
    Locale.setDefault(Locale.forLanguageTag(language.tag))
}
