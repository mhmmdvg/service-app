package com.cashierserviceapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import qrcode.raw.ErrorCorrectionLevel
import qrcode.raw.QRCodeProcessor
import kotlin.math.round

/**
 * The modules ("pixels") of an encoded QR code, as a square grid.
 *
 * Kept separate from the drawing so the same encode can feed both the preview and the bytes sent
 * to a printer — the library's own square type carries render state we don't want to hold onto.
 */
@Immutable
class QrMatrix internal constructor(
    val size: Int,
    private val cells: BooleanArray,
) {
    /** True where the module is dark. */
    operator fun get(row: Int, column: Int): Boolean = cells[row * size + column]
}

/**
 * Encodes [data] as a QR code.
 *
 * @param errorCorrection how much of the code can be lost and still scan. [ErrorCorrectionLevel.MEDIUM]
 * recovers ~15%, which is the right trade for thermal paper — enough to survive a smudge or a
 * thumbprint without inflating the grid to where a 58mm roll can't print it legibly.
 */
fun qrMatrixOf(
    data: String,
    errorCorrection: ErrorCorrectionLevel = ErrorCorrectionLevel.MEDIUM,
): QrMatrix {
    val squares = QRCodeProcessor(data, errorCorrection).encode()
    val size = squares.size

    return QrMatrix(
        size = size,
        cells = BooleanArray(size * size) { squares[it / size][it % size].dark }
    )
}

/** Modules of blank margin the QR spec requires around the code for a scanner to lock onto it. */
private const val QUIET_ZONE = 4

/**
 * A scannable QR code.
 *
 * Colours are fixed dark-on-light rather than themed, and deliberately so: scanners expect dark
 * modules on a light field, and enough of them fail on an inverted code that following the app into
 * dark mode would mean a receipt that can't be scanned. On screen it reads as the paper it stands
 * in for.
 */
@Composable
fun QrCode(
    data: String,
    modifier: Modifier = Modifier,
    moduleColor: Color = Color.Black,
    backgroundColor: Color = Color.White,
    errorCorrection: ErrorCorrectionLevel = ErrorCorrectionLevel.MEDIUM,
) {
    if (data.isEmpty()) return

    val matrix = remember(data, errorCorrection) { qrMatrixOf(data, errorCorrection) }

    Canvas(modifier) {
        val gridSize = matrix.size + QUIET_ZONE * 2
        val cell = size.minDimension / gridSize
        val side = cell * gridSize

        // Centred, because the caller sizes the box and we keep the code square inside it.
        val originX = round((size.width - side) / 2f)
        val originY = round((size.height - side) / 2f)

        drawRect(backgroundColor, Offset(originX, originY), Size(side, side))

        // Every edge lands on a whole pixel, and runs of dark modules are drawn as one rect. Both
        // are about the same thing: a QR drawn as N² separate anti-aliased squares picks up seams
        // between them, and a scanner reads those seams as noise.
        for (row in 0 until matrix.size) {
            val top = originY + round((QUIET_ZONE + row) * cell)
            val bottom = originY + round((QUIET_ZONE + row + 1) * cell)

            var column = 0
            while (column < matrix.size) {
                if (!matrix[row, column]) {
                    column++
                    continue
                }

                val runStart = column
                while (column < matrix.size && matrix[row, column]) column++

                val left = originX + round((QUIET_ZONE + runStart) * cell)
                val right = originX + round((QUIET_ZONE + column) * cell)

                drawRect(
                    color = moduleColor,
                    topLeft = Offset(left, top),
                    size = Size(right - left, bottom - top),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun QrCodePreview() = PreviewHelper {
    QrCode(
        data = "322F9819-579B-4DB7-8328-6530C5F386BF",
        modifier = Modifier.size(140.dp),
    )
}
