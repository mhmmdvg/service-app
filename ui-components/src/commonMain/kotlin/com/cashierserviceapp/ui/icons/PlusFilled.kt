package com.cashierserviceapp.ui.icons

import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

val PlusFilled: ImageVector
    get() {
        if (_PlusFilled != null) return _PlusFilled!!

        _PlusFilled = ImageVector.Builder(
            name = "PlusFilled",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(11.9922f, 4.00012f)
                verticalLineTo(20.0001f)
                moveTo(19.9922f, 12.0001f)
                horizontalLineTo(3.99222f)
            }
        }.build()

        return _PlusFilled!!
    }

private var _PlusFilled: ImageVector? = null

@PreviewLightDark
@Composable
fun PlusFilledPreview() = PreviewHelper {
    Icon(
        imageVector = PlusFilled,
        contentDescription = "PlusFilled",
        tint = CashierServiceTheme.colors.primaryText
    )
}

