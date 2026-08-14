package com.cashierserviceapp.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.components.BottomSheet
import com.cashierserviceapp.ui.components.Button
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

/**
 * Confirmation for an action worth pausing on, asked in a bottom sheet so the answer sits under the
 * thumb rather than in the middle of the screen.
 *
 * Both buttons hide the sheet first and report the answer once it has slid away — calling
 * [onConfirm] or [onDismiss] straight from the click would drop the sheet from composition
 * mid-animation and it would simply vanish.
 */
@Composable
fun ConfirmSheet(
    title: String,
    body: String,
    confirmLabel: String,
    cancelLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmEnabled: Boolean = true,
) {
    // Which way the sheet is leaving, remembered across the exit. Anything that isn't the confirm
    // button — cancel, back, a tap outside, a drag — counts as a dismissal.
    var confirmed by remember { mutableStateOf(false) }

    BottomSheet(
        onDismissRequest = { if (confirmed) onConfirm() else onDismiss() }
    ) { hide ->
        Spacer(Modifier.height(20.dp))

        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 24.dp),
            style = CashierServiceTheme.typography.h3,
            color = CashierServiceTheme.colors.primaryText
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = body,
            modifier = Modifier.padding(horizontal = 24.dp),
            style = CashierServiceTheme.typography.text2,
            color = CashierServiceTheme.colors.secondaryText
        )

        Spacer(Modifier.height(28.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                label = cancelLabel,
                onClick = hide,
                modifier = Modifier.weight(1f),
                primary = false
            )
            Button(
                label = confirmLabel,
                onClick = { confirmed = true; hide() },
                modifier = Modifier.weight(1f),
                primary = true,
                enabled = confirmEnabled,
                primaryBackground = CashierServiceTheme.colors.dangerBackground
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}

@PreviewLightDark
@Composable
private fun ConfirmSheetPreview() = PreviewHelper {
    ConfirmSheet(
        title = "Sign out?",
        body = "You'll need your email and password to get back in.",
        confirmLabel = "Sign out",
        cancelLabel = "Cancel",
        onConfirm = {},
        onDismiss = {}
    )
}
