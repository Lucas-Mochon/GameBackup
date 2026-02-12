package fr.sdv.gamebacklog.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "accessibility_settings"
)

class AccessibilityPreferencesManager(private val context: Context) {

    companion object {
        private val CONTRAST_MODE_KEY = stringPreferencesKey("contrast_mode")
        private val FONT_SCALE_KEY = floatPreferencesKey("font_scale")
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }

    val contrastMode: Flow<AccessibilityPreferences.ContrastMode> = context.dataStore.data.map {
        val value = it[CONTRAST_MODE_KEY] ?: AccessibilityPreferences.ContrastMode.NORMAL.name
        AccessibilityPreferences.ContrastMode.valueOf(value)
    }

    val fontScaleFactor: Flow<Float> = context.dataStore.data.map {
        it[FONT_SCALE_KEY] ?: 1.0f
    }

    val themeMode: Flow<AccessibilityPreferences.ThemeMode> = context.dataStore.data.map {
        val value = it[THEME_MODE_KEY] ?: AccessibilityPreferences.ThemeMode.SYSTEM.name
        AccessibilityPreferences.ThemeMode.valueOf(value)
    }

    suspend fun setContrastMode(mode: AccessibilityPreferences.ContrastMode) {
        context.dataStore.edit {
            it[CONTRAST_MODE_KEY] = mode.name
        }
    }

    suspend fun setFontScaleFactor(scale: Float) {
        context.dataStore.edit {
            it[FONT_SCALE_KEY] = scale
        }
    }

    suspend fun setThemeMode(mode: AccessibilityPreferences.ThemeMode) {
        context.dataStore.edit {
            it[THEME_MODE_KEY] = mode.name
        }
    }
}

