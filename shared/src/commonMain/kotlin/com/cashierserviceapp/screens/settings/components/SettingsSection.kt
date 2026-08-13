package com.cashierserviceapp.screens.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

/**
 * A titled group of settings, optionally with a line of explanation underneath the control — the
 * place to say what "System" means rather than leaving the user to guess.
 */
@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    footnote: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = CashierServiceTheme.typography.text2.copy(fontWeight = FontWeight.SemiBold),
            color = CashierServiceTheme.colors.secondaryText
        )

        Spacer(Modifier.height(10.dp))

        content()

        if (footnote != null) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = footnote,
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.noteText
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SettingsSectionPreview() = PreviewHelper {
    SettingsSection(
        title = "Appearance",
        footnote = "System follows your device's setting."
    ) {
        Text("control goes here")
    }
}
