package co.id.scanberry.scanberryv2app.model

import com.google.gson.annotations.SerializedName

data class DetectionResult(
    @SerializedName("box")       val box: List<Float>,
    @SerializedName("class_id")  val classId: Int,
    @SerializedName("confidence")val confidence: Float,
    val imageRotation: Int = 0
){
    val left: Float
        get() = box.getOrElse(0) { 0f }
    val top: Float
        get() = box.getOrElse(1) { 0f }
    val right: Float
        get() = box.getOrElse(2) { 0f }
    val bottom: Float
        get() = box.getOrElse(3) { 0f }
}