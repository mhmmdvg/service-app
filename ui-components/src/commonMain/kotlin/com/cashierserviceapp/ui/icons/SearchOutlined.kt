package com.cashierserviceapp.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SearchOutlined: ImageVector
    get() {
        if (_SearchOutlined != null) return _SearchOutlined!!

        _SearchOutlined = ImageVector.Builder(
            name = "SearchOutlined",
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
                // Lens
                moveTo(11f, 4f)
                curveTo(14.866f, 4f, 18f, 7.134f, 18f, 11f)
                curveTo(18f, 14.866f, 14.866f, 18f, 11f, 18f)
                curveTo(7.134f, 18f, 4f, 14.866f, 4f, 11f)
                curveTo(4f, 7.134f, 7.134f, 4f, 11f, 4f)
                close()
                // Handle
                moveTo(16.5f, 16.5f)
                lineTo(20f, 20f)
            }
        }.build()

        return _SearchOutlined!!
    }

private var _SearchOutlined: ImageVector? = null
