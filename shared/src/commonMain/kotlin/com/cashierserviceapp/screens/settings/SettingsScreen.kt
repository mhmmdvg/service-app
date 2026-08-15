package com.cashierserviceapp.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cashierserviceapp.ScreenWithTitle
import com.cashierserviceapp.Theme
import com.cashierserviceapp.domain.models.Profile
import com.cashierserviceapp.domain.models.UserRole
import com.cashierserviceapp.localization.AppLanguage
import com.cashierserviceapp.screens.settings.components.ConfirmSheet
import com.cashierserviceapp.screens.settings.components.ProfileCard
import com.cashierserviceapp.screens.settings.components.ProfileCardSkeleton
import com.cashierserviceapp.screens.settings.components.SettingsSection
import com.cashierserviceapp.ui.components.Button
import com.cashierserviceapp.ui.components.SegmentedSelector
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.utils.Resource
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.action_cancel
import cashierserviceapp.shared.generated.resources.action_try_again
import cashierserviceapp.shared.generated.resources.profile_load_failed
import cashierserviceapp.shared.generated.resources.settings_account_title
import cashierserviceapp.shared.generated.resources.settings_appearance_footnote
import cashierserviceapp.shared.generated.resources.settings_appearance_title
import cashierserviceapp.shared.generated.resources.settings_language_footnote
import cashierserviceapp.shared.generated.resources.settings_language_title
import cashierserviceapp.shared.generated.resources.settings_sign_out
import cashierserviceapp.shared.generated.resources.settings_sign_out_confirm_body
import cashierserviceapp.shared.generated.resources.settings_sign_out_confirm_title
import cashierserviceapp.shared.generated.resources.settings_theme_dark
import cashierserviceapp.shared.generated.resources.settings_theme_light
import cashierserviceapp.shared.generated.resources.settings_theme_system
import cashierserviceapp.shared.generated.resources.settings_title
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    onSignedOut: () -> Unit,
    viewModel: SettingsViewModel = metroViewModel(),
) {
    val theme by viewModel.theme.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val isSigningOut by viewModel.isSigningOut.collectAsStateWithLifecycle()

    SettingsContent(
        theme = theme,
        language = language,
        profileState = profileState,
        isSigningOut = isSigningOut,
        onThemeChange = viewModel::setTheme,
        onLanguageChange = viewModel::setLanguage,
        onRetryProfile = viewModel::loadProfile,
        onSignOut = { viewModel.signOut(onSignedOut) },
    )
}

@Composable
private fun SettingsContent(
    theme: Theme,
    language: AppLanguage,
    profileState: Resource<Profile>,
    isSigningOut: Boolean,
    onThemeChange: (Theme) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onRetryProfile: () -> Unit,
    onSignOut: () -> Unit,
) {
    var confirmingSignOut by remember { mutableStateOf(false) }

    // SegmentedSelector's label lambda isn't composable, so the theme copy is resolved up front.
    val themeLabels = mapOf(
        Theme.SYSTEM to stringResource(Res.string.settings_theme_system),
        Theme.LIGHT to stringResource(Res.string.settings_theme_light),
        Theme.DARK to stringResource(Res.string.settings_theme_dark),
    )

    ScreenWithTitle(title = stringResource(Res.string.settings_title)) {
        Spacer(Modifier.height(8.dp))

        // A cached session gives us a name and email straight away, so the card is usually filled
        // before /me answers. The failure state only shows when there was nothing to fall back on.
        val profile = profileState.data
        when {
            profile != null -> ProfileCard(profile = profile)

            profileState is Resource.Error -> ProfileLoadFailed(
                message = profileState.message,
                onRetry = onRetryProfile
            )

            else -> ProfileCardSkeleton()
        }

        Spacer(Modifier.height(32.dp))

        SettingsSection(
            title = stringResource(Res.string.settings_appearance_title),
            footnote = stringResource(Res.string.settings_appearance_footnote)
        ) {
            SegmentedSelector(
                options = Theme.entries,
                selected = theme,
                onSelect = onThemeChange,
                label = { themeLabels.getValue(it) }
            )
        }

        Spacer(Modifier.height(28.dp))

        SettingsSection(
            title = stringResource(Res.string.settings_language_title),
            footnote = stringResource(Res.string.settings_language_footnote)
        ) {
            SegmentedSelector(
                options = AppLanguage.entries,
                selected = language,
                onSelect = onLanguageChange,
                label = { it.label }
            )
        }

        Spacer(Modifier.height(28.dp))

        SettingsSection(title = stringResource(Res.string.settings_account_title)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(CashierServiceTheme.shapes.roundedCornerLg)
                    .clickable(
                        enabled = !isSigningOut,
                        role = Role.Button,
                        onClick = { confirmingSignOut = true }
                    )
                    .padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(Res.string.settings_sign_out),
                    style = CashierServiceTheme.typography.text1.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = CashierServiceTheme.colors.dangerText
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }

    if (confirmingSignOut) {
        ConfirmSheet(
            title = stringResource(Res.string.settings_sign_out_confirm_title),
            body = stringResource(Res.string.settings_sign_out_confirm_body),
            confirmLabel = stringResource(Res.string.settings_sign_out),
            cancelLabel = stringResource(Res.string.action_cancel),
            confirmEnabled = !isSigningOut,
            onConfirm = {
                confirmingSignOut = false
                onSignOut()
            },
            onDismiss = { confirmingSignOut = false }
        )
    }
}

@Composable
private fun ProfileLoadFailed(
    message: String?,
    onRetry: () -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.profile_load_failed),
            style = CashierServiceTheme.typography.h4,
            color = CashierServiceTheme.colors.primaryText
        )

        if (message != null) {
            Spacer(Modifier.height(4.dp))

            Text(
                text = message,
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.secondaryText
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            label = stringResource(Res.string.action_try_again),
            onClick = onRetry,
            primary = false
        )
    }
}

private val previewProfile = Profile(
    name = "Muhammad Vikri",
    email = "admin@cashierservice.app",
    role = UserRole.ADMIN,
    phone = "0812 4712 342"
)

@PreviewLightDark
@Composable
private fun SettingsScreenPreview() = PreviewHelper(paddingEnabled = false) {
    SettingsContent(
        theme = Theme.SYSTEM,
        language = AppLanguage.EN,
        profileState = Resource.Success(previewProfile),
        isSigningOut = false,
        onThemeChange = {},
        onLanguageChange = {},
        onRetryProfile = {},
        onSignOut = {}
    )
}

/**
 * Renders in whichever language the preview host is set to, not necessarily [AppLanguage.ID].
 * Compose Resources picks its catalog from the platform locale, and a preview has no way to ask for
 * a different one without moving the locale for every other preview in the process — so checking
 * the Indonesian copy means running the app and switching in Settings.
 */
@PreviewLightDark
@Composable
private fun SettingsScreenIndonesianPreview() = PreviewHelper(paddingEnabled = false) {
    SettingsContent(
        theme = Theme.DARK,
        language = AppLanguage.ID,
        profileState = Resource.Success(previewProfile),
        isSigningOut = false,
        onThemeChange = {},
        onLanguageChange = {},
        onRetryProfile = {},
        onSignOut = {}
    )
}

@PreviewLightDark
@Composable
private fun SettingsScreenProfileErrorPreview() = PreviewHelper(paddingEnabled = false) {
    SettingsContent(
        theme = Theme.LIGHT,
        language = AppLanguage.EN,
        profileState = Resource.Error("Couldn't reach the server."),
        isSigningOut = false,
        onThemeChange = {},
        onLanguageChange = {},
        onRetryProfile = {},
        onSignOut = {}
    )
}
