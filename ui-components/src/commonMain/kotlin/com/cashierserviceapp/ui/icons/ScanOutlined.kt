package com.cashierserviceapp.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Viewfinder corners with a scan line through the middle — the usual "point the camera" mark. */
val ScanOutlined: ImageVector
    get() {
        if (_ScanOutlined != null) return _ScanOutlined!!

        _ScanOutlined = ImageVector.Builder(
            name = "ScanOutlined",
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
                // Top-left
                moveTo(4f, 9f)
                lineTo(4f, 6f)
                curveTo(4f, 4.895f, 4.895f, 4f, 6f, 4f)
                lineTo(9f, 4f)
                // Top-right
                moveTo(15f, 4f)
                lineTo(18f, 4f)
                curveTo(19.105f, 4f, 20f, 4.895f, 20f, 6f)
                lineTo(20f, 9f)
                // Bottom-right
                moveTo(20f, 15f)
                lineTo(20f, 18f)
                curveTo(20f, 19.105f, 19.105f, 20f, 18f, 20f)
                lineTo(15f, 20f)
                // Bottom-left
                moveTo(9f, 20f)
                lineTo(6f, 20f)
                curveTo(4.895f, 20f, 4f, 19.105f, 4f, 18f)
                lineTo(4f, 15f)
                // Scan line
                moveTo(3f, 12f)
                lineTo(21f, 12f)
            }
        }.build()

        return _ScanOutlined!!
    }

private var _ScanOutlined: ImageVector? = null
