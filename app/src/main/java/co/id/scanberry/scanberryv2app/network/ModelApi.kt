package co.id.scanberry.scanberryv2app.network

import co.id.scanberry.scanberryv2app.model.PredictionResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface ModelApi {
    @Multipart
    @POST("predict/")
    suspend fun predict(
        @Header("X-API-Key") apiKey: String,
        @Part image: MultipartBody.Part
    ): PredictionResponse
}