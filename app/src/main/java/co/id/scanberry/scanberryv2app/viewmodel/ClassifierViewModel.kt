package co.id.scanberry.scanberryv2app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.id.scanberry.scanberryv2app.model.DetectionResult
import co.id.scanberry.scanberryv2app.repository.ModelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

@HiltViewModel
class ClassifierViewModel @Inject constructor(
    private val repo: ModelRepository
) : ViewModel() {

    private val _detections = MutableStateFlow<List<DetectionResult>>(emptyList())
    val detections: StateFlow<List<DetectionResult>> = _detections.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun classifyImage(file: File) {
        viewModelScope.launch {
            _loading.value = true
            _detections.value = try {
                val raw = repo.classifyImage(file)
                applyNms(raw)
            } catch (_: Exception) {
                emptyList()
            }
            _loading.value = false
        }
    }

    fun classifyPart(part: MultipartBody.Part, rotationDegrees: Int) {
        viewModelScope.launch {
            _loading.value = true
            _detections.value = try {
                val raw = repo.classifyImagePart(part)
                    .map { it.copy(imageRotation = rotationDegrees) }
                applyNms(raw)
            } catch (_: Exception) {
                emptyList()
            }
            _loading.value = false
        }
    }

    /**
     * Non-Maximum Suppression untuk menghapus deteksi yang tumpang tindih.
     */
    private fun applyNms(detections: List<DetectionResult>, iouThreshold: Float = 0.45f): List<DetectionResult> {
        // 1. Urutkan semua deteksi berdasarkan confidence, dari tertinggi ke terendah.
        val sortedDetections = detections.sortedByDescending { it.confidence }
        val selectedDetections = mutableListOf<DetectionResult>()

        for (det in sortedDetections) {
            var shouldSuppress = false
            // 2. Bandingkan dengan setiap deteksi yang sudah terpilih.
            for (selected in selectedDetections) {
                // 3. Jika tumpang tindih (IoU) melebihi ambang batas, deteksi ini harus dibuang.
                if (iou(det, selected) > iouThreshold) {
                    shouldSuppress = true
                    break
                }
            }

            // 4. Jika tidak ada tumpang tindih yang signifikan, simpan deteksi ini.
            if (!shouldSuppress) {
                selectedDetections.add(det)
            }
        }

        return selectedDetections
    }

    /**
     * Hitung Intersection over Union (IoU) antara dua bounding box.
     * Fungsi ini sudah benar dan tidak perlu diubah.
     */
    private fun iou(a: DetectionResult, b: DetectionResult): Float {
        val xA = max(a.left, b.left)
        val yA = max(a.top, b.top)
        val xB = min(a.right, b.right)
        val yB = min(a.bottom, b.bottom)

        val interArea = max(0f, xB - xA) * max(0f, yB - yA)
        val boxAArea = (a.right - a.left) * (a.bottom - a.top)
        val boxBArea = (b.right - b.left) * (b.bottom - b.top)
        val unionArea = boxAArea + boxBArea - interArea

        return if (unionArea == 0f) 0f else interArea / unionArea
    }

}
