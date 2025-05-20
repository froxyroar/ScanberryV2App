package co.id.scanberry.scanberryv2app.model

import com.google.gson.annotations.SerializedName

data class DetectionResult(
    @SerializedName("box")       val box: List<Float>,
    @SerializedName("class_id")  val classId: Int,
    @SerializedName("confidence")val confidence: Float,
    val imageRotation: Int = 0
)