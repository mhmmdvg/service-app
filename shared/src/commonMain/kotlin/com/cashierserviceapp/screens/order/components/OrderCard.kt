package com.cashierserviceapp.screens.order.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.ui.components.Chip
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.icons.NotepadFilled
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

@Composable
fun OrderCard(
    name: String,
    code: String,
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(CashierServiceTheme.shapes.roundedCornerLg)
            .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.05f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            Modifier
                .clip(CircleShape)
                .background(CashierServiceTheme.colors.primaryBackground.copy(0.25f))
                .padding(6.dp)
                .size(26.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = NotepadFilled,
                contentDescription = "order_icon",
                tint = CashierServiceTheme.colors.primaryBackground
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = name,
                style = CashierServiceTheme.typography.h4
            )
            Text(
                text = code,
                style = CashierServiceTheme.typography.text2
            )
        }
        Spacer(Modifier.weight(1f))
        when(status) {
            OrderStatus.RECEIVED -> {
                Chip(
                    label = "Received Order",
                    color = CashierServiceTheme.colors.blueText
                )
            }
            OrderStatus.DIAGNOSING -> {
                Chip(
                    label = "Diagnosing",
                    color = CashierServiceTheme.colors.orangeText
                )
            }
            OrderStatus.IN_PROGRESS -> {
               Chip(
                   label = "In Progress",
                   color = CashierServiceTheme.colors.purpleText
               )
            }
            OrderStatus.COMPLETED -> {
                Chip(
                    label = "Completed Order",
                    color = CashierServiceTheme.colors.greenText
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun OrderCardPreview() = PreviewHelper {
    OrderCard(
        name = "Vikri",
        code = "iPhone 13",
        status = OrderStatus.RECEIVED,
    )
}