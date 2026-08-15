package com.cashierserviceapp.ui.icons

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

val ChevronLeftOutlined: ImageVector
    get() {
        if (_ChevronLeftOutlined != null) return _ChevronLeftOutlined!!
        
        _ChevronLeftOutlined = ImageVector.Builder(
            name = "ChevronLeftOutlined",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(15f, 18f)
                curveTo(15f, 18f, 9.00001f, 13.5811f, 9f, 12f)
                curveTo(8.99999f, 10.4188f, 15f, 6f, 15f, 6f)
            }
        }.build()
        
        return _ChevronLeftOutlined!!
    }

private var _ChevronLeftOutlined: ImageVector? = null

@PreviewLightDark
@Composable
private fun ChevronLeftOutlinedPreview() = PreviewHelper {
    Icon(
        imageVector = ChevronLeftOutlined,
        contentDescription = null,
        tint = CashierServiceTheme.colors.primaryText,
    )
}

