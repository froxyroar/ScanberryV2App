package co.id.scanberry.scanberryv2app.model

import com.google.gson.annotations.SerializedName

data class PredictionResponse(
    @SerializedName("model")            val model: String,
    @SerializedName("num_predictions")  val numPredictions: Int,
    @SerializedName("predictions")      val predictions: List<DetectionResult>
)