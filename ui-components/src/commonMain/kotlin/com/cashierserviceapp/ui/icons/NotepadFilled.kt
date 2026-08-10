package com.cashierserviceapp.ui.icons

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark

val NotepadFilled: ImageVector
    get() {
        if (_NotepadFilled != null) return _NotepadFilled!!

        _NotepadFilled = ImageVector.Builder(
            name = "NotepadFilled",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Top binding pins (rounded capsules, 1.5 wide, from y=2 to y=5)
            path(fill = SolidColor(Color.Black)) {
                moveTo(6.74219f, 2.75f)
                curveTo(6.74219f, 2.33579f, 7.07798f, 2f, 7.49219f, 2f)
                curveTo(7.90640f, 2f, 8.24219f, 2.33579f, 8.24219f, 2.75f)
                verticalLineTo(4.25f)
                curveTo(8.24219f, 4.66421f, 7.90640f, 5f, 7.49219f, 5f)
                curveTo(7.07798f, 5f, 6.74219f, 4.66421f, 6.74219f, 4.25f)
                close()

                moveTo(11.24219f, 2.75f)
                curveTo(11.24219f, 2.33579f, 11.57798f, 2f, 11.99219f, 2f)
                curveTo(12.40640f, 2f, 12.74219f, 2.33579f, 12.74219f, 2.75f)
                verticalLineTo(4.25f)
                curveTo(12.74219f, 4.66421f, 12.40640f, 5f, 11.99219f, 5f)
                curveTo(11.57798f, 5f, 11.24219f, 4.66421f, 11.24219f, 4.25f)
                close()

                moveTo(15.74219f, 2.75f)
                curveTo(15.74219f, 2.33579f, 16.07798f, 2f, 16.49219f, 2f)
                curveTo(16.90640f, 2f, 17.24219f, 2.33579f, 17.24219f, 2.75f)
                verticalLineTo(4.25f)
                curveTo(17.24219f, 4.66421f, 16.90640f, 5f, 16.49219f, 5f)
                curveTo(16.07798f, 5f, 15.74219f, 4.66421f, 15.74219f, 4.25f)
                close()
            }

            // Body + the two text lines punched out with even-odd fill
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd
            ) {
                // outer body
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

                // hole: long line at y = 11
                moveTo(7.99219f, 10.25f)
                horizontalLineTo(15.99219f)
                curveTo(16.40640f, 10.25f, 16.74219f, 10.58579f, 16.74219f, 11f)
                curveTo(16.74219f, 11.41421f, 16.40640f, 11.75f, 15.99219f, 11.75f)
                horizontalLineTo(7.99219f)
                curveTo(7.57798f, 11.75f, 7.24219f, 11.41421f, 7.24219f, 11f)
                curveTo(7.24219f, 10.58579f, 7.57798f, 10.25f, 7.99219f, 10.25f)
                close()

                // hole: short line at y = 15
                moveTo(7.99219f, 14.25f)
                horizontalLineTo(11.99219f)
                curveTo(12.40640f, 14.25f, 12.74219f, 14.58579f, 12.74219f, 15f)
                curveTo(12.74219f, 15.41421f, 12.40640f, 15.75f, 11.99219f, 15.75f)
                horizontalLineTo(7.99219f)
                curveTo(7.57798f, 15.75f, 7.24219f, 15.41421f, 7.24219f, 15f)
                curveTo(7.24219f, 14.58579f, 7.57798f, 14.25f, 7.99219f, 14.25f)
                close()
            }
        }.build()

        return _NotepadFilled!!
    }

private var _NotepadFilled: ImageVector? = null

@PreviewLightDark
@Composable
fun PreviewNotepadFilled() = PreviewHelper {
    Image(
        imageVector = NotepadFilled,
        contentDescription = null,
    )
}