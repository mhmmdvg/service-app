package com.cashierserviceapp.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val NotepadOutlined: ImageVector
    get() {
        if (_NotepadOutlined != null) return _NotepadOutlined!!
        
        _NotepadOutlined = ImageVector.Builder(
            name = "NotepadOutlined",
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
                moveTo(16.4922f, 2f)
                verticalLineTo(5f)
                moveTo(7.49219f, 2f)
                verticalLineTo(5f)
                moveTo(11.9922f, 2f)
                verticalLineTo(5f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12.9922f, 3.5f)
                horizontalLineTo(10.9922f)
                curveTo(7.69236f, 3.5f, 6.04244f, 3.5f, 5.01731f, 4.52513f)
                curveTo(3.99219f, 5.55025f, 3.99219f, 7.20017f, 3.99219f, 10.5f)
                verticalLineTo(15f)
                curveTo(3.99219f, 18.2998f, 3.99219f, 19.9497f, 5.01731f, 20.9749f)
                curveTo(6.04244f, 22f, 7.69236f, 22f, 10.9922f, 22f)
                horizontalLineTo(12.9922f)
                curveTo(16.292f, 22f, 17.9419f, 22f, 18.9671f, 20.9749f)
                curveTo(19.9922f, 19.9497f, 19.9922f, 18.2998f, 19.9922f, 15f)
                verticalLineTo(10.5f)
                curveTo(19.9922f, 7.20017f, 19.9922f, 5.55025f, 18.9671f, 4.52512f)
                curveTo(17.9419f, 3.5f, 16.292f, 3.5f, 12.9922f, 3.5f)
                close()
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(7.99219f, 15f)
                horizontalLineTo(11.9922f)
                moveTo(7.99219f, 11f)
                horizontalLineTo(15.9922f)
            }
        }.build()
        
        return _NotepadOutlined!!
    }

private var _NotepadOutlined: ImageVector? = null

