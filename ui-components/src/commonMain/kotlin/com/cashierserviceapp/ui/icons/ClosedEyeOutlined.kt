package com.composables

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ClosedEyeOutlined: ImageVector
    get() {
        if (_ClosedEyeOutlined != null) return _ClosedEyeOutlined!!
        
        _ClosedEyeOutlined = ImageVector.Builder(
            name = "ClosedEyeOutlined",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Miter
            ) {
                moveTo(22f, 8f)
                curveTo(22f, 8f, 18f, 14f, 12f, 14f)
                curveTo(6f, 14f, 2f, 8f, 2f, 8f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Miter
            ) {
                moveTo(15f, 13.5f)
                lineTo(16.5f, 16f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Miter
            ) {
                moveTo(20f, 11f)
                lineTo(22f, 13f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Miter
            ) {
                moveTo(2f, 13f)
                lineTo(4f, 11f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Miter
            ) {
                moveTo(9f, 13.5f)
                lineTo(7.5f, 16f)
            }
        }.build()
        
        return _ClosedEyeOutlined!!
    }

private var _ClosedEyeOutlined: ImageVector? = null

