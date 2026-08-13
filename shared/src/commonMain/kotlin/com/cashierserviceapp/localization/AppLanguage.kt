package com.cashierserviceapp.localization

import kotlinx.serialization.Serializable

/**
 * The languages the app ships.
 *
 * @param tag BCP-47 tag, matching the qualifier Compose Resources would use for a `values-<tag>`
 *   directory, so the two stay aligned if the catalogs ever move there.
 * @param label the language's own name, shown in the picker — never translated, because someone
 *   looking for their language recognises it in that language.
 */
@Serializable
enum class AppLanguage(val tag: String, val label: String) {
    EN("en", "English"),
    ID("id", "Bahasa Indonesia");

    companion object {
        /** Falls back to [EN] for an unknown or missing tag. */
        fun fromTag(tag: String?): AppLanguage = entries.firstOrNull { it.tag == tag } ?: EN
    }
}
