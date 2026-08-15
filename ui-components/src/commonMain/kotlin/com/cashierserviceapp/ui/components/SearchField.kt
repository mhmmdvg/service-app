package com.cashierserviceapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.icons.ScanOutlined
import com.cashierserviceapp.ui.icons.SearchOutlined
import com.cashierserviceapp.ui.icons.XOutlined
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

/**
 * A pill-shaped search box: magnifier on the left, a clear button once there's text, and room for
 * one action on the right.
 *
 * Deliberately not [TextField] — that one is a bordered, floating-label form field. Search reads as
 * a filled pill, and putting the two side by side would look like a mistake.
 */
@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    focusRequester: FocusRequester = remember { FocusRequester() },
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailing: (@Composable () -> Unit)? = null,
) {
    val textColor = CashierServiceTheme.colors.primaryText
    val baseStyle = CashierServiceTheme.typography.text2
    val textStyle = remember(baseStyle, textColor) { baseStyle.copy(color = textColor) }
    val cursorBrush = remember(textColor) { SolidColor(textColor) }

    SearchPill(modifier, trailing = trailing) {
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = textStyle,
                    color = CashierServiceTheme.colors.placeholderText,
                    maxLines = 1
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                enabled = enabled,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = keyboardActions,
                textStyle = textStyle,
                cursorBrush = cursorBrush,
            )
        }

        if (value.isNotEmpty()) {
            Spacer(Modifier.width(8.dp))

            Icon(
                imageVector = XOutlined,
                contentDescription = "Clear search",
                modifier = Modifier
                    .size(18.dp)
                    .clickable(
                        interactionSource = null,
                        indication = null,
                        role = Role.Button,
                        onClick = { onValueChange("") }
                    ),
                tint = CashierServiceTheme.colors.secondaryText
            )
        }
    }
}

/**
 * Looks exactly like [SearchField] but doesn't take input — tapping it is expected to open a screen
 * that does. The usual mobile pattern: searching gets the whole display and the keyboard, rather
 * than typing into a field with a list squeezed underneath.
 *
 * [trailing] keeps its own click target, so a scan button inside the pill still works without
 * bouncing through the search screen first.
 */
@Composable
fun SearchFieldButton(
    modifier: Modifier = Modifier,
    placeholder: String,
    onClick: () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
) {
    SearchPill(
        modifier = modifier,
        trailing = trailing,
        onClick = onClick,
    ) {
        Text(
            text = placeholder,
            modifier = Modifier.weight(1f),
            style = CashierServiceTheme.typography.text2,
            color = CashierServiceTheme.colors.placeholderText,
            maxLines = 1
        )
    }
}

/** The shared shell, so the real field and its stand-in can never drift apart. */
@Composable
private fun SearchPill(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    trailing: (@Composable () -> Unit)?,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick, role = Role.Button)
            .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.08f))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = SearchOutlined,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = CashierServiceTheme.colors.secondaryText
        )

        Spacer(Modifier.width(10.dp))

        content()

        if (trailing != null) {
            Spacer(Modifier.width(12.dp))
            trailing()
        }
    }
}

@PreviewLightDark
@Composable
private fun SearchFieldPreview() = PreviewHelper {
    SearchFieldButton(
        placeholder = "Search name or order code",
        onClick = {},
        trailing = {
            Icon(
                imageVector = ScanOutlined,
                contentDescription = "Scan QR",
                modifier = Modifier.size(22.dp),
                tint = CashierServiceTheme.colors.primaryText
            )
        }
    )

    var empty by remember { mutableStateOf("") }
    SearchField(
        value = empty,
        onValueChange = { empty = it },
        placeholder = "Search name or order code"
    )

    var filled by remember { mutableStateOf("Rina") }
    SearchField(
        value = filled,
        onValueChange = { filled = it },
        placeholder = "Search name or order code"
    )
}
