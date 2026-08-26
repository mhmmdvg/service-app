package com.cashierserviceapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

/** The spinner under a paginated list while its next page loads. Small on purpose — the rows above
 * stay readable, so it reads as "more is coming" rather than "the screen stopped". */
@Composable
fun ListFooterLoader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(22.dp),
            color = CashierServiceTheme.colors.secondaryText,
            strokeWidth = 2.dp
        )
    }
}

@PreviewLightDark
@Composable
private fun ListFooterLoaderPreview() = PreviewHelper {
    ListFooterLoader()
}
