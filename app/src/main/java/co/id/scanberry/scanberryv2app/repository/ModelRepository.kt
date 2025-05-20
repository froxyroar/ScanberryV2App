package co.id.scanberry.scanberryv2app.repository

import co.id.scanberry.scanberryv2app.BuildConfig
import co.id.scanberry.scanberryv2app.model.DetectionResult
import co.id.scanberry.scanberryv2app.network.ModelApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelRepository @Inject constructor(
    private val api: ModelApi
) {
    suspend fun classifyImage(imageFile: File): List<DetectionResult> {
        val part = MultipartBody.Part.createFormData(
            "file", imageFile.name,
            imageFile.asRequestBody("image/jpeg".toMediaType())
        )
        return api.predict(BuildConfig.API_KEY, part).predictions
    }

    suspend fun classifyImagePart(part: MultipartBody.Part): List<DetectionResult> {
        return api.predict(BuildConfig.API_KEY, part).predictions
    }
}