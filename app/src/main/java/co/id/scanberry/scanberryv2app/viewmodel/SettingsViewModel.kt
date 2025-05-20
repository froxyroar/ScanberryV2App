package co.id.scanberry.scanberryv2app.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.id.scanberry.scanberryv2app.data.DataStoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application): AndroidViewModel(app) {
    private val store = DataStoreManager(app)

    val isDarkMode = store.isDarkMode.stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = false
    )

    val language = store.language.stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = "en"
    )

    // ✅ Expose firstRun as StateFlow
    val firstRun = store.firstRun.stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = true
    )

    fun setDarkMode(d: Boolean) = viewModelScope.launch {
        store.setDarkMode(d)
    }

    fun setLanguage(lang: String) = viewModelScope.launch {
        store.setLanguage(lang)
    }

    // ✅ Setter for firstRun
    fun setFirstRun(value: Boolean) = viewModelScope.launch {
        store.setFirstRun(value)
    }
}
