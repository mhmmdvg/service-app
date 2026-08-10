package com.cashierserviceapp.ui.icons

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

val HistoryFilled: ImageVector
    get() {
        if (_HistoryFilled != null) return _HistoryFilled!!

        _HistoryFilled = ImageVector.Builder(
            name = "HistoryFilled",
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
                moveTo(4.43186f, 14.9656f)
                curveTo(5.65759f, 18.4791f, 9.00032f, 21f, 12.9318f, 21f)
                curveTo(17.9024f, 21f, 21.9318f, 16.9706f, 21.9318f, 12f)
                curveTo(21.9318f, 7.02944f, 17.9024f, 3f, 12.9318f, 3f)
                curveTo(9.23111f, 3f, 5.83124f, 5.6756f, 4.62227f, 8.5f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12.9319f, 7f)
                verticalLineTo(12f)
                lineTo(15.9319f, 14f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(8.43054f, 8.74363f)
                curveTo(8.43054f, 8.74363f, 4.74691f, 9.3026f, 4.1879f, 8.7436f)
                curveTo(3.62888f, 8.1846f, 4.18791f, 4.50098f, 4.18791f, 4.50098f)
            }
        }.build()

        return _HistoryFilled!!
    }

private var _HistoryFilled: ImageVector? = null

@PreviewLightDark
@Composable
fun HistoryFilledPreview() = PreviewHelper {
    Image(
        imageVector = HistoryFilled,
        contentDescription = null,
    )
}
