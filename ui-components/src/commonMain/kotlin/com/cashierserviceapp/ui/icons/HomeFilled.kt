package com.cashierserviceapp.ui.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.utils.PreviewLightDark

val HomeFilled: ImageVector
    get() {
        if (_HomeFilled != null) return _HomeFilled!!

        _HomeFilled = ImageVector.Builder(
            name = "HomeFilled",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd
            ) {
                // House body (same outline as HomeOutlined, filled instead of stroked)
                moveTo(3f, 11.9896f)
                verticalLineTo(14.5f)
                curveTo(3f, 17.7998f, 3f, 19.4497f, 4.02513f, 20.4749f)
                curveTo(5.05025f, 21.5f, 6.70017f, 21.5f, 10f, 21.5f)
                horizontalLineTo(14f)
                curveTo(17.2998f, 21.5f, 18.9497f, 21.5f, 19.9749f, 20.4749f)
                curveTo(21f, 19.4497f, 21f, 17.7998f, 21f, 14.5f)
                verticalLineTo(11.9896f)
                curveTo(21f, 10.3083f, 21f, 9.46773f, 20.6441f, 8.74005f)
                curveTo(20.2882f, 8.01237f, 19.6247f, 7.49628f, 18.2976f, 6.46411f)
                lineTo(16.2976f, 4.90855f)
                curveTo(14.2331f, 3.30285f, 13.2009f, 2.5f, 12f, 2.5f)
                curveTo(10.7991f, 2.5f, 9.76689f, 3.30285f, 7.70242f, 4.90855f)
                lineTo(5.70241f, 6.46411f)
                curveTo(4.37533f, 7.49628f, 3.71179f, 8.01237f, 3.3559f, 8.74005f)
                curveTo(3f, 9.46773f, 3f, 10.3083f, 3f, 11.9896f)
                close()

                // Smile, knocked out via even-odd.
                // Arc of a circle centered (12, 13) r = 5, thickened to 1.5 with round caps.
                moveTo(8.55f, 17.6f)
                arcTo(5.75f, 5.75f, 0f, false, false, 15.45f, 17.6f)
                arcTo(0.75f, 0.75f, 0f, true, false, 14.55f, 16.4f)
                arcTo(4.25f, 4.25f, 0f, false, true, 9.45f, 16.4f)
                arcTo(0.75f, 0.75f, 0f, true, false, 8.55f, 17.6f)
                close()
            }
        }.build()

        return _HomeFilled!!
    }

private var _HomeFilled: ImageVector? = null

@PreviewLightDark
@Composable
private fun HomeFilledPreview() {
    Column {
        Image(
            imageVector = HomeFilled,
            contentDescription = "preview"
        )
    }
}