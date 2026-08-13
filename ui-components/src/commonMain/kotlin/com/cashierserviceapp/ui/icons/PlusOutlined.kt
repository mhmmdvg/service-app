package com.cashierserviceapp.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val PlusOutlined: ImageVector
    get() {
        if (_PlusOutlined != null) return _PlusOutlined!!
        
        _PlusOutlined = ImageVector.Builder(
            name = "PlusOutlined",
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
                moveTo(11.9922f, 4.00012f)
                verticalLineTo(20.0001f)
                moveTo(19.9922f, 12.0001f)
                horizontalLineTo(3.99222f)
            }
        }.build()
        
        return _PlusOutlined!!
    }

private var _PlusOutlined: ImageVector? = null

