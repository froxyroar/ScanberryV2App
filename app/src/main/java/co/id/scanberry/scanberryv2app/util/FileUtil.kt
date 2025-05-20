// util/FileUtil.kt
package co.id.scanberry.scanberryv2app.util

import android.content.Context
import android.net.Uri
import java.io.File

object FileUtil {
    fun from(context: Context, uri: Uri): File {
        val input = context.contentResolver.openInputStream(uri)!!
        val temp = File(context.cacheDir, "upload.jpg")
        temp.outputStream().use { output -> input.copyTo(output) }
        return temp
    }
}
