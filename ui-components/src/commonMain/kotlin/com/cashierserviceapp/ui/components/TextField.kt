package com.cashierserviceapp.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.composables.XOutlined

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    focusRequester: FocusRequester = remember { FocusRequester() },
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    singleLine: Boolean = false,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailing: (@Composable () -> Unit)? = null,
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    // BasicTextField defaults both the text color and the cursor to solid black, which is
    // invisible on the dark theme's black100 background. Both have to be themed explicitly.
    val textColor = CashierServiceTheme.colors.primaryText
    val baseStyle = CashierServiceTheme.typography.text2
    val textStyle = remember(baseStyle, textColor) { baseStyle.copy(color = textColor) }
    val cursorBrush = remember(textColor) { SolidColor(textColor) }

    FieldContainer(
        modifier = modifier,
        label = label,
        isFocused = isFocused,
        isFilled = value.isNotEmpty(),
        trailing = trailing,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            enabled = enabled,
            interactionSource = interactionSource,
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            textStyle = textStyle,
            cursorBrush = cursorBrush,
        )
    }
}


@PreviewLightDark
@Composable
private fun TextFieldPreview() = PreviewHelper {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it },
        label = "Search your city"
    )

    var filled by remember { mutableStateOf("Jakarta") }

    TextField(
        value = filled,
        onValueChange = { filled = it },
        label = "City",
        singleLine = true,
        trailing = {
            Icon(
                imageVector = XOutlined,
                contentDescription = null,
                tint = CashierServiceTheme.colors.secondaryText
            )
        }
    )
}
