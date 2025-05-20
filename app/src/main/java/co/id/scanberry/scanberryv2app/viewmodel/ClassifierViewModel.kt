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
                repo.classifyImage(file) // untuk mode upload gambar
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
                repo.classifyImagePart(part).map { det ->
                    det.copy(imageRotation = rotationDegrees) // tambahkan rotasi ke hasil deteksi
                }
            } catch (_: Exception) {
                emptyList()
            }
            _loading.value = false
        }
    }
}
