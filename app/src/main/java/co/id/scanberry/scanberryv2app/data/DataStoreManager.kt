package co.id.scanberry.scanberryv2app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// Extension property to get DataStore<Preferences>
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(private val ctx: Context) {
    companion object {
        private val KEY_DARK = booleanPreferencesKey("dark_mode")
        private val KEY_LANG = stringPreferencesKey("language")
        private val KEY_FIRST_RUN = booleanPreferencesKey("first_run")
    }

    val isDarkMode: Flow<Boolean> = ctx.dataStore.data.map { prefs ->
        prefs[KEY_DARK] == true
    }

    val language: Flow<String> = ctx.dataStore.data.map { prefs ->
        prefs[KEY_LANG] ?: "en"
    }

    // ✅ Add firstRun flag
    val firstRun: Flow<Boolean> = ctx.dataStore.data.map { prefs ->
        prefs[KEY_FIRST_RUN] != false
    }

    // ✅ Function to set firstRun flag
    suspend fun setFirstRun(value: Boolean) {
        ctx.dataStore.edit { prefs ->
            prefs[KEY_FIRST_RUN] = value
        }
    }

    suspend fun setDarkMode(value: Boolean) {
        ctx.dataStore.edit { prefs ->
            prefs[KEY_DARK] = value
        }
    }

    suspend fun setLanguage(lang: String) {
        ctx.dataStore.edit { prefs ->
            prefs[KEY_LANG] = lang
        }
    }
}
