package fr.sdv.gamebacklog.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import fr.sdv.gamebacklog.utils.AccessibilityPreferences

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

// High Contrast Light Colors
private val HighContrastLightColorScheme = lightColorScheme(
    primary = Color(0xFF000000),
    secondary = Color(0xFF000000),
    tertiary = Color(0xFF000000),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),
    onSurface = Color(0xFF000000)
)

// High Contrast Dark Colors
private val HighContrastDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    secondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFFFFFFFF),
    background = Color(0xFF000000),
    surface = Color(0xFF000000),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onTertiary = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF)
)

@Composable
fun GameBacklogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    contrastMode: AccessibilityPreferences.ContrastMode = AccessibilityPreferences.ContrastMode.NORMAL,
    themeMode: AccessibilityPreferences.ThemeMode = AccessibilityPreferences.ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val shouldUseDarkTheme = when (themeMode) {
        AccessibilityPreferences.ThemeMode.LIGHT -> false
        AccessibilityPreferences.ThemeMode.DARK -> true
        AccessibilityPreferences.ThemeMode.SYSTEM -> darkTheme
    }

    val colorScheme = when {
        contrastMode == AccessibilityPreferences.ContrastMode.HIGH -> {
            if (shouldUseDarkTheme) HighContrastDarkColorScheme else HighContrastLightColorScheme
        }
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (shouldUseDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        shouldUseDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}