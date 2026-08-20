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
                moveTo(17f, 17f)
                lineTo(21f, 21f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(19f, 11f)
                curveTo(19f, 6.58172f, 15.4183f, 3f, 11f, 3f)
                curveTo(6.58172f, 3f, 3f, 6.58172f, 3f, 11f)
                curveTo(3f, 15.4183f, 6.58172f, 19f, 11f, 19f)
                curveTo(15.4183f, 19f, 19f, 15.4183f, 19f, 11f)
                close()
            }
        }.build()
        
        return _SearchOutlined!!
    }

private var _SearchOutlined: ImageVector? = null

