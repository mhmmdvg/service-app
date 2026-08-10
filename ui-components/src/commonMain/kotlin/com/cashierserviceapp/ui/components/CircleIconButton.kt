package com.cashierserviceapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.icons.HomeOutlined
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

@Composable
fun CircleIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    icon: ImageVector,
    contentDescription: String? = null,
) {
    IconButton(
        modifier = modifier
            .clip(CircleShape)
            .background(CashierServiceTheme.colors.tileBackground),
        onClick = onClick,
        enabled = enabled,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = CashierServiceTheme.colors.primaryText
        )
    }
}


@PreviewLightDark
@Composable
private fun PreviewCircleIconButton() = PreviewHelper {
    CircleIconButton(
        modifier = Modifier.size(56.dp),
        onClick = {},
        icon = HomeOutlined
    )
}