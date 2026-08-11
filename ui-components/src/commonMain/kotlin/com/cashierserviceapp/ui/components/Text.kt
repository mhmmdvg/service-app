package com.cashierserviceapp.ui.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.cashierserviceapp.ui.theme.CashierServiceTheme

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = CashierServiceTheme.colors.primaryText,
    style: TextStyle = TextStyle.Default,
    maxLines: Int = Int.MAX_VALUE,
    selectable: Boolean = false,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) {
    Text(
        text = AnnotatedString(text),
        modifier = modifier,
        color = color,
        style = style,
        maxLines = maxLines,
        selectable = selectable,
        onTextLayout = onTextLayout
    )
}

@Composable
fun Text(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = CashierServiceTheme.colors.primaryText,
    style: TextStyle = CashierServiceTheme.typography.text1,
    maxLines: Int = Int.MAX_VALUE,
    selectable: Boolean = false,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) {
    val content = @Composable {
        BasicText(
            text = text,
            modifier = modifier,
            style = style,
            color = { color },
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            inlineContent = inlineContent,
            onTextLayout = onTextLayout,
        )
    }

    if (selectable) {
        SelectionContainer { content() }
    } else {
        content()
    }
}