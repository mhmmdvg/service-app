package com.cashierserviceapp.localization

import kotlinx.serialization.Serializable

/**
 * The languages the app ships.
 *
 * @param tag BCP-47 tag. Doubles as the Compose Resources qualifier, so [ID] is exactly the `id` in
 *   `composeResources/values-id/` — adding a language means adding an entry here and the matching
 *   directory, nothing else.
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
