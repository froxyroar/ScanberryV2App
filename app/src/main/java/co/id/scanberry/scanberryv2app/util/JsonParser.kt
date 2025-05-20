package co.id.scanberry.scanberryv2app.util

import co.id.scanberry.scanberryv2app.model.PredictionResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object JsonParser {
    private val json = Json { ignoreUnknownKeys = true }
    fun parseResponse(jsonString: String): PredictionResponse =
        json.decodeFromString(jsonString)
}