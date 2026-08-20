package com.cashierserviceapp.screens.orderdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.order_detail_update_status
import com.cashierserviceapp.ui.components.Button
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import org.jetbrains.compose.resources.stringResource

/**
 * The screen's primary action, parked where a thumb reaches.
 *
 * Overlaid rather than passed to Scaffold as a `bottomBar`: ScreenWithTitle doesn't expose that
 * slot, and adding one would change how every other screen pads its content.
 */
@Composable
internal fun UpdateStatusFooter(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(CashierServiceTheme.colors.mainBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
    ) {
        HorizontalDivider(thickness = 1.dp, color = CashierServiceTheme.colors.strokePale)

        Button(
            label = stringResource(Res.string.order_detail_update_status),
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 17.dp, vertical = 12.dp),
            primary = true,
            enabled = enabled,
        )
    }
}