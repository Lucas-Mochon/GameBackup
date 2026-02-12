package fr.sdv.gamebacklog.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

object AccessibilityPreferences {
    enum class ContrastMode {
        NORMAL, HIGH
    }

    enum class ThemeMode {
        LIGHT, DARK, SYSTEM
    }
}

data class AccessibilitySettings(
    val contrastMode: AccessibilityPreferences.ContrastMode = AccessibilityPreferences.ContrastMode.NORMAL,
    val fontScaleFactor: Float = 1.0f,
    val themeMode: AccessibilityPreferences.ThemeMode = AccessibilityPreferences.ThemeMode.SYSTEM
)

object AccessibilityDefaults {
    const val MIN_TOUCH_TARGET_SIZE = 48  // dp

    @Composable
    fun getScaledFontSize(baseSp: Float, scaleFactor: Float): androidx.compose.ui.unit.TextUnit {
        return (baseSp * scaleFactor).sp
    }
}

