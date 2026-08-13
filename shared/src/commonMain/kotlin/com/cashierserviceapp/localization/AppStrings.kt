package com.cashierserviceapp.localization

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * The app's translated copy, one instance per [AppLanguage].
 *
 * Deliberately not Compose Resources' `Res.string`. That mechanism picks its language from
 * `Locale.current` via an environment whose constructor and composition local are both `internal`
 * to the library, so an app cannot point it at a chosen language — the only lever is the *platform*
 * locale, which needs `AppCompatDelegate` on Android and an app restart on iOS. A plain data class
 * behind a composition local switches instantly, identically on both platforms, and costs a
 * recomposition.
 *
 * Being a data class with no defaults is the point: adding a string here fails to compile until
 * every language supplies it, so a translation can't silently go missing.
 */
data class AppStrings(
    // Navigation
    val navHome: String,
    val navOrder: String,
    val navAddOrder: String,
    val navHistory: String,
    val navSettings: String,

    // Settings — sections
    val settingsAppearance: String,
    val settingsLanguage: String,
    val settingsAccount: String,

    // Settings — theme
    val themeSystem: String,
    val themeLight: String,
    val themeDark: String,
    val themeDescription: String,

    // Settings — language
    val languageDescription: String,

    // Settings — profile
    val profileLoadFailed: String,
    val profileRetry: String,
    val roleAdmin: String,
    val roleCashier: String,

    // Settings — sign out
    val signOut: String,
    val signOutConfirmTitle: String,
    val signOutConfirmBody: String,
    val signOutConfirm: String,
    val cancel: String,
)

val EnglishStrings = AppStrings(
    navHome = "Home",
    navOrder = "Order",
    navAddOrder = "Add Order",
    navHistory = "History",
    navSettings = "Settings",

    settingsAppearance = "Appearance",
    settingsLanguage = "Language",
    settingsAccount = "Account",

    themeSystem = "System",
    themeLight = "Light",
    themeDark = "Dark",
    themeDescription = "System follows your device's setting.",

    languageDescription = "Applies immediately, across the whole app.",

    profileLoadFailed = "Couldn't load your profile",
    profileRetry = "Try again",
    roleAdmin = "Admin",
    roleCashier = "Cashier",

    signOut = "Sign out",
    signOutConfirmTitle = "Sign out?",
    signOutConfirmBody = "You'll need your email and password to get back in.",
    signOutConfirm = "Sign out",
    cancel = "Cancel",
)

val IndonesianStrings = AppStrings(
    navHome = "Beranda",
    navOrder = "Pesanan",
    navAddOrder = "Tambah Pesanan",
    navHistory = "Riwayat",
    navSettings = "Pengaturan",

    settingsAppearance = "Tampilan",
    settingsLanguage = "Bahasa",
    settingsAccount = "Akun",

    themeSystem = "Sistem",
    themeLight = "Terang",
    themeDark = "Gelap",
    themeDescription = "Sistem mengikuti pengaturan perangkat kamu.",

    languageDescription = "Langsung diterapkan ke seluruh aplikasi.",

    profileLoadFailed = "Profil gagal dimuat",
    profileRetry = "Coba lagi",
    roleAdmin = "Admin",
    roleCashier = "Kasir",

    signOut = "Keluar",
    signOutConfirmTitle = "Keluar dari akun?",
    signOutConfirmBody = "Kamu perlu email dan kata sandi untuk masuk lagi.",
    signOutConfirm = "Keluar",
    cancel = "Batal",
)

fun stringsFor(language: AppLanguage): AppStrings = when (language) {
    AppLanguage.EN -> EnglishStrings
    AppLanguage.ID -> IndonesianStrings
}

/** Static: the whole tree re-reads when the language changes, which is exactly what should happen. */
val LocalStrings = staticCompositionLocalOf { EnglishStrings }
