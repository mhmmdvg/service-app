package com.composables

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val XOutlined: ImageVector
    get() {
        if (_XOutlined != null) return _XOutlined!!
        
        _XOutlined = ImageVector.Builder(
            name = "XOutlined",
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
                moveTo(18f, 6f)
                lineTo(12f, 12f)
                moveTo(12f, 12f)
                lineTo(6f, 18f)
                moveTo(12f, 12f)
                lineTo(18f, 18f)
                moveTo(12f, 12f)
                lineTo(6f, 6f)
            }
        }.build()
        
        return _XOutlined!!
    }

private var _XOutlined: ImageVector? = null

