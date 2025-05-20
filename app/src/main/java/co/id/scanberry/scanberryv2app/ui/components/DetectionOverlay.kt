package co.id.scanberry.scanberryv2app.ui.components

import android.graphics.Matrix
import android.graphics.RectF
import androidx.camera.view.PreviewView
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
import kotlin.math.max
import kotlin.math.min

@Composable
fun DetectionOverlay(
    detections: List<DetectionResult>,
    imageWidth: Float, // Should be 640f as per RealtimeScreen
    imageHeight: Float, // Should be 640f as per RealtimeScreen
    contentScaleFit: Boolean = false // false = FILL_CENTER
) {
    Canvas(Modifier.fillMaxSize()) {
        val cw = size.width
        val ch = size.height

        // 1) Hitung scale masing-masing axis dari dimensi model input (imageWidth, imageHeight) ke dimensi Canvas (cw, ch)
        val sx = cw / imageWidth
        val sy = ch / imageHeight

        // 2) Karena FILL_CENTER, ambil max scale
        val scale = max(sx, sy)

        // 3) Hitung offset untuk centering
        val dx = (cw - imageWidth * scale) / 2f
        val dy = (ch - imageHeight * scale) / 2f

        detections.forEach { det ->
            var (l, t, r, b) = det.box

            // --- START Perbaikan Logika Rotasi ---
            // Box coordinates (l, t, r, b) are relative to the original (unrotated) 640x640 image.
            // We need to rotate these coordinates first based on imageRotation,
            // then scale/translate the rotated coordinates to the screen.

            val originalImageWidth = imageWidth // 640f
            val originalImageHeight = imageHeight // 640f

            when (det.imageRotation) {
                270 -> { // Rotate 90 degrees clockwise
                    // New (l, t, r, b) relative to the 90-degree rotated 640x640 image
                    val nl = t // New left is original top
                    val nt = originalImageWidth - r // New top is original right relative to new top edge (original right is at W-r from new top)
                    val nr = b // New right is original bottom
                    val nb = originalImageWidth - l // New bottom is original left relative to new bottom edge (original left is at W-l from new top)
                    l = nl; t = nt; r = nr; b = nb
                }
                180 -> { // Rotate 180 degrees
                    // New (l, t, r, b) relative to the 180-degree rotated 640x640 image
                    val nl = originalImageWidth - r // New left is original right relative to new right edge
                    val nt = originalImageHeight - b // New top is original bottom relative to new bottom edge
                    val nr = originalImageWidth - l // New right is original left relative to new left edge
                    val nb = originalImageHeight - t // New bottom is original top relative to new top edge
                    l = nl; t = nt; r = nr; b = nb
                }
                90 -> { // Rotate 270 degrees clockwise (or 90 counter-clockwise)
                    // New (l, t, r, b) relative to the 270-degree rotated 640x640 image
                    val nl = originalImageHeight - b // New left is original bottom relative to new left edge
                    val nt = l // New top is original left
                    val nr = originalImageHeight - t // New right is original top relative to new right edge
                    val nb = r // New bottom is original right
                    l = nl; t = nt; r = nr; b = nb
                }
            }
            // --- END Perbaikan Logika Rotasi ---

            // 5) Transform koordinat box yang sudah dirotasi ke pixel UI (Canvas) menggunakan scale dan offset
            val left   = l * scale + dx
            val top    = t * scale + dy
            val right  = r * scale + dx
            val bottom = b * scale + dy

            // 6) Gambar kotak
            drawRect(
                color = when(det.classId) {
                    0 -> Color.Green; 1->Color.Yellow
                    2 -> Color.Red;   3->Color.Cyan
                    4 -> Color.Blue;  else->Color.Magenta
                },
                topLeft = Offset(left, top),
                size    = Size(right-left, bottom-top),
                style   = Stroke(width = 4f)
            )

            // 7) Gambar label
            // Posisi label di atas kiri kotak (setelah ditransformasi ke layar)
            val txt = listOf("UNR","FRA","FRB","HRA","HRB")
                .getOrNull(det.classId) ?: "UNK"
            val text = "$txt ${"%.2f".format(det.confidence)}"
            drawContext.canvas.nativeCanvas.apply {
                val bg = android.graphics.Paint().apply {
                    setColor(android.graphics.Color.BLACK); setAlpha(160)
                }
                val fg = android.graphics.Paint().apply {
                    setColor(android.graphics.Color.WHITE)
                    textSize = 32f; isAntiAlias = true
                }
                val w = fg.measureText(text)
                // Gambar background label sedikit di atas kotak
                drawRect(left, top - 40f, left + w + 8f, top, bg)
                // Gambar teks label
                drawText(text, left + 4f, top - 12f, fg) // Teks diletakkan sedikit di dalam background
            }
        }
    }
}