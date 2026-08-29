package com.cashierserviceapp.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.components.BottomSheet
import com.cashierserviceapp.ui.components.Button
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.components.TextField
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

/**
 * Renames the shop that heads every printed receipt.
 *
 * Saves on the way out rather than on the button press, the same shape as [ConfirmSheet]: calling
 * back mid-animation would drop the sheet from composition and it would vanish instead of sliding.
 */
@Composable
fun ShopNameSheet(
    label: String,
    footnote: String,
    value: String,
    saveLabel: String,
    cancelLabel: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var draft by remember { mutableStateOf(value) }
    var saving by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    BottomSheet(
        onDismissRequest = { if (saving) onSave(draft.trim()) else onDismiss() }
    ) { hide ->
        // The sheet is here to type in, so it opens with the caret already in the field.
        LaunchedEffect(Unit) { focusRequester.requestFocus() }

        Spacer(Modifier.height(24.dp))

        // No heading: the field's own label already says what this sheet is for.
        TextField(
            value = draft,
            onValueChange = { draft = it },
            modifier = Modifier.padding(horizontal = 24.dp),
            label = label,
            focusRequester = focusRequester,
            singleLine = true,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = footnote,
            modifier = Modifier.padding(horizontal = 24.dp),
            style = CashierServiceTheme.typography.text2,
            color = CashierServiceTheme.colors.noteText
        )

        Spacer(Modifier.height(24.dp))

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
                label = saveLabel,
                onClick = { saving = true; hide() },
                modifier = Modifier.weight(1f),
                primary = true,
                // A blank heading would print an empty line, so there is nothing to save.
                enabled = draft.isNotBlank()
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}

@PreviewLightDark
@Composable
private fun ShopNameSheetPreview() = PreviewHelper {
    ShopNameSheet(
        label = "Shop name",
        footnote = "Printed as the heading on every receipt.",
        value = "CASHIER SERVICE",
        saveLabel = "Save",
        cancelLabel = "Cancel",
        onSave = {},
        onDismiss = {}
    )
}
