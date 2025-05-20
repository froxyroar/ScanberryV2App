// app/src/main/java/co/id/scanberry/scanberryv2app/util/BitmapUtils.kt
package co.id.scanberry.scanberryv2app.util

import android.graphics.Bitmap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

/**
 * Utility to convert a CameraX ImageProxy (YUV_420_888) into JPEG bytes
 * and wrap them as MultipartBody.Part.  This works on *any* CameraX version.
 */
object BitmapUtils {

    fun Bitmap.toMultipart(paramName: String): MultipartBody.Part {
        val file = File.createTempFile("realtime_", ".jpg")
        val outputStream = FileOutputStream(file)
        compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(paramName, file.name, requestBody)
    }

}
