package com.cashierserviceapp.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Body, paper feeding out of the top, and the printed slip below. */
val PrinterOutlined: ImageVector
    get() {
        if (_PrinterOutlined != null) return _PrinterOutlined!!

        _PrinterOutlined = ImageVector.Builder(
            name = "PrinterOutlined",
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
                // Paper going in
                moveTo(7f, 8f)
                lineTo(7f, 4f)
                lineTo(17f, 4f)
                lineTo(17f, 8f)
                // Body
                moveTo(7f, 8f)
                lineTo(5f, 8f)
                curveTo(3.895f, 8f, 3f, 8.895f, 3f, 10f)
                lineTo(3f, 14f)
                curveTo(3f, 15.105f, 3.895f, 16f, 5f, 16f)
                lineTo(7f, 16f)
                moveTo(17f, 8f)
                lineTo(19f, 8f)
                curveTo(20.105f, 8f, 21f, 8.895f, 21f, 10f)
                lineTo(21f, 14f)
                curveTo(21f, 15.105f, 20.105f, 16f, 19f, 16f)
                lineTo(17f, 16f)
                // Printed slip
                moveTo(7f, 13f)
                lineTo(17f, 13f)
                lineTo(17f, 20f)
                lineTo(7f, 20f)
                close()
            }
        }.build()

        return _PrinterOutlined!!
    }

private var _PrinterOutlined: ImageVector? = null
