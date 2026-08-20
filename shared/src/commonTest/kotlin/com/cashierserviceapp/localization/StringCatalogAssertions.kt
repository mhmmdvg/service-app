package com.cashierserviceapp.localization

import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.allStringResources
import kotlin.test.assertTrue
import org.jetbrains.compose.resources.getString

/**
 * Keys whose Indonesian reading is legitimately the same as the English one. Every *other* key that
 * reads identically in both languages is one `values-id/strings.xml` forgot: a missing translation
 * doesn't fail, it quietly falls back to the default catalog.
 */
private val IDENTICAL_IN_BOTH_LANGUAGES = setOf(
    "profile_role_admin", // "Admin" either way
    "auth_email", // "Email"
    "add_order_device_model", // "Model"
    "order_detail_total", // "Total"
    "receipt_status", // "Status"
    "receipt_subtotal", // "Subtotal"
    "receipt_total", // "TOTAL"
    // Month names Indonesian borrowed unchanged. The other nine differ, so a whole catalog of
    // English months can't hide behind this list.
    "month_4", // April
    "month_9", // September
    "month_11", // November
)

/**
 * Guards what moving the catalogs from a Kotlin data class to XML gave up. The data class had no
 * defaults, so the compiler rejected a language that was missing a string; resources have no such
 * check, and a gap only shows up as English leaking into an Indonesian screen.
 *
 * Lives in commonTest but is driven from iosTest, because it doubles as proof that
 * [applyAppLanguage] really does move resource resolution, and iOS is where that is least obvious —
 * it works by writing `AppleLanguages` into `NSUserDefaults`. Running it on desktop as well would
 * mean putting all of compose-desktop on this module's test classpath, since resolving the system
 * environment there initializes skiko, which cannot load its native library in a headless test JVM.
 */
internal suspend fun assertIndonesianCatalogIsComplete() {
    applyAppLanguage(AppLanguage.EN)
    val english = Res.allStringResources.mapValues { (_, resource) -> getString(resource) }

    applyAppLanguage(AppLanguage.ID)
    val indonesian = Res.allStringResources.mapValues { (_, resource) -> getString(resource) }

    applyAppLanguage(AppLanguage.EN)

    assertTrue(english.isNotEmpty(), "No string resources were found at all")

    val untranslated = english
        .filterKeys { it !in IDENTICAL_IN_BOTH_LANGUAGES }
        .filter { (key, text) -> indonesian[key] == text }
        .keys
        .sorted()

    assertTrue(
        untranslated.isEmpty(),
        "values-id/strings.xml is missing a translation for: $untranslated"
    )
}
