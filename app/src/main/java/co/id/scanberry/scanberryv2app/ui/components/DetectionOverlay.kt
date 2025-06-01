package co.id.scanberry.scanberryv2app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import co.id.scanberry.scanberryv2app.model.DetectionResult

@Composable
fun DetectionOverlay(
    detections: List<DetectionResult>,
    imageWidth: Float = 640f,
    imageHeight: Float = 640f
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        size.width  // canvas width
        size.height // canvas height

        // Compute manual transformation matrix
        val canvasWidth = size.width
        val canvasHeight = size.height

        val scaleX = canvasWidth / imageWidth
        val scaleY = canvasHeight / imageHeight
        val scale = minOf(scaleX, scaleY) // FIT_CENTER keeps full image visible

        // Compute offset to center the image inside the canvas
        val dx = (canvasWidth - imageWidth * scale) / 2f
        val dy = (canvasHeight - imageHeight * scale) / 2f

        detections.forEach { det ->
            var (l, t, r, b) = det.box

            // Optional: apply rotation correction if needed
            when (det.imageRotation) {
                270 -> {
                    val nl = t
                    val nt = imageWidth - r
                    val nr = b
                    val nb = imageWidth - l; l = nl; t = nt; r = nr; b = nb
                }

                180 -> {
                    val nl = imageWidth - r
                    val nt = imageHeight - b
                    val nr = imageWidth - l
                    val nb = imageHeight - t; l = nl; t = nt; r = nr; b = nb
                }

                90 -> {
                    val nl = imageHeight - b
                    val nt = l
                    val nr = imageHeight - t
                    val nb = r; l = nl; t = nt; r = nr; b = nb
                }
            }

            // Apply scaling and offset
            val left = l * scale + dx
            val top = t * scale + dy
            val right = r * scale + dx
            val bottom = b * scale + dy

            // Draw bounding box
            drawRect(
                color = when (det.classId) {
                    0 -> Color.Red
                    1 -> Color.Yellow
                    2 -> Color.Blue
                    3 -> Color.Cyan
                    4 -> Color.Green
                    else -> Color.Magenta
                },
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                style = Stroke(width = 4f)
            )

            // Draw label
            val txt = listOf("FRA", "FRB", "HRA", "HRB", "UNR").getOrNull(det.classId) ?: "UNK"
            val label = "$txt ${"%.2f".format(det.confidence)}"
            drawContext.canvas.nativeCanvas.apply {
                val bg = android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    alpha = 160
                }
                val fg = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 32f
                    isAntiAlias = true
                }
                val w = fg.measureText(label)
                drawRect(left, top - 40f, left + w + 8f, top, bg)
                drawText(label, left + 4f, top - 12f, fg)
            }
        }
    }
}
