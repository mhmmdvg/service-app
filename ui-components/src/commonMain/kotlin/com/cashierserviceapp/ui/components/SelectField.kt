package com.cashierserviceapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun SelectField(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String
) {
    FieldContainer(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
        label = label,
        isFocused = false,
        isFilled = value.isNotEmpty()
    ) {
        Text(value)
    }
}