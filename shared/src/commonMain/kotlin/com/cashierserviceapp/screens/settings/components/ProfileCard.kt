package com.cashierserviceapp.screens.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cashierserviceapp.domain.models.Profile
import com.cashierserviceapp.domain.models.UserRole
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.profile_role_admin
import cashierserviceapp.shared.generated.resources.profile_role_cashier
import org.jetbrains.compose.resources.stringResource
import com.cashierserviceapp.ui.components.Avatar
import com.cashierserviceapp.ui.components.Chip
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.extensions.shimmerEffect
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

/**
 * Who you're signed in as. Same avatar and tile treatment as the order rows, one size up, so the
 * screen opens on something recognisably the same app.
 */
@Composable
fun ProfileCard(
    profile: Profile,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(CashierServiceTheme.shapes.roundedCornerLg)
            .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.05f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Avatar(
            name = profile.name,
            size = 56.dp,
            initialSize = 24.sp
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = profile.name,
                style = CashierServiceTheme.typography.h4,
                maxLines = 1
            )
            Text(
                text = profile.email,
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.secondaryText,
                maxLines = 1
            )
            profile.phone?.takeIf { it.isNotBlank() }?.let { phone ->
                Text(
                    text = phone,
                    style = CashierServiceTheme.typography.text2,
                    color = CashierServiceTheme.colors.secondaryText,
                    maxLines = 1
                )
            }
        }

        Chip(
            label = when (profile.role) {
                UserRole.ADMIN -> stringResource(Res.string.profile_role_admin)
                UserRole.CASHIER -> stringResource(Res.string.profile_role_cashier)
            },
            color = when (profile.role) {
                UserRole.ADMIN -> CashierServiceTheme.colors.purpleText
                UserRole.CASHIER -> CashierServiceTheme.colors.blueText
            }
        )
    }
}

@Composable
fun ProfileCardSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(CashierServiceTheme.shapes.roundedCornerLg)
            .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.05f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            Modifier
                .clip(CircleShape)
                .size(56.dp)
                .shimmerEffect()
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                Modifier
                    .width(120.dp)
                    .height(14.dp)
                    .clip(CashierServiceTheme.shapes.roundedCornerSm)
                    .shimmerEffect()
            )
            Box(
                Modifier
                    .width(168.dp)
                    .height(11.dp)
                    .clip(CashierServiceTheme.shapes.roundedCornerSm)
                    .shimmerEffect()
            )
        }

        Spacer(Modifier.weight(1f))
    }
}

@PreviewLightDark
@Composable
private fun ProfileCardPreview() = PreviewHelper {
    ProfileCard(
        profile = Profile(
            name = "Muhammad Vikri",
            email = "admin@cashierservice.app",
            role = UserRole.ADMIN,
            phone = "0812 4712 342"
        )
    )
    ProfileCardSkeleton()
}
