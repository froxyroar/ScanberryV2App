// app/src/main/java/co/id/scanberry/scanberryv2app/util/BitmapUtils.kt
package co.id.scanberry.scanberryv2app.util

import android.content.Context
import android.graphics.*
import androidx.camera.core.ImageProxy
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

/**
 * Utility to convert a CameraX ImageProxy (YUV_420_888) into JPEG bytes
 * and wrap them as MultipartBody.Part.  This works on *any* CameraX version.
 */
object BitmapUtils {
    fun toMultipartPart(
        context: Context,
        image: ImageProxy,
        fieldName: String,
        quality: Int = 80
    ): MultipartBody.Part? {
        try {
            // 1) Convert YUV_420_888 ImageProxy → Bitmap
            val bitmap = yuv420ToBitmap(context, image)

            // 2) Compress Bitmap → JPEG bytes
            val baos = ByteArrayOutputStream().apply {
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, this)
            }
            val jpegBytes = baos.toByteArray()

            // 3) Wrap into RequestBody & MultipartBody.Part
            val reqBody = jpegBytes.toRequestBody("image/jpeg".toMediaType())
            return MultipartBody.Part.createFormData(fieldName, "frame.jpg", reqBody)

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            // 4) Always close the proxy
            image.close()
        }
    }

    /**
     * Helper to turn a YUV ImageProxy into an RGB Bitmap using a ScriptIntrinsicYuvToRGB
     * RenderScript conversion (wrapped in a reusable converter).
     */
    private fun yuv420ToBitmap(context: Context, image: ImageProxy): Bitmap {
        // Prepare an empty bitmap that will hold the converted frame
        val bitmap = Bitmap.createBitmap(
            image.width, image.height, Bitmap.Config.ARGB_8888
        )

        // Grab the three YUV planes
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        // Allocate a single array large enough for all YUV data
        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize) // V plane
        uBuffer.get(nv21, ySize + vSize, uSize) // U plane

        // Convert NV21 byte array to Bitmap
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(
            Rect(0, 0, image.width, image.height),
            100,
            out
        )
        val jpegBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)
    }
}
