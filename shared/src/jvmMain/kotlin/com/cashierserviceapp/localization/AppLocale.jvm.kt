package com.cashierserviceapp.localization

import java.util.Locale

actual fun applyAppLanguage(language: AppLanguage) {
    val target = Locale.forLanguageTag(language.tag)
    if (Locale.getDefault().language != target.language) Locale.setDefault(target)
}
