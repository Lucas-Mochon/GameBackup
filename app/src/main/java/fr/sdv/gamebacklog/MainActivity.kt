package fr.sdv.gamebacklog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import fr.sdv.gamebacklog.data.database.GameBacklogDatabase
import fr.sdv.gamebacklog.data.repository.GameRepository
import fr.sdv.gamebacklog.navigation.GameBacklogNavigation
import fr.sdv.gamebacklog.ui.theme.GameBacklogTheme
import fr.sdv.gamebacklog.utils.AccessibilityPreferences
import fr.sdv.gamebacklog.utils.AccessibilityPreferencesManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val database = GameBacklogDatabase.getDatabase(this@MainActivity)
            val repository = GameRepository(database.gameDao())
            val accessibilityManager = AccessibilityPreferencesManager(this@MainActivity)
            val scope = rememberCoroutineScope()

            val contrastMode by accessibilityManager.contrastMode.collectAsState(
                initial = AccessibilityPreferences.ContrastMode.NORMAL
            )
            val fontScaleFactor by accessibilityManager.fontScaleFactor.collectAsState(initial = 1.0f)
            val themeMode by accessibilityManager.themeMode.collectAsState(
                initial = AccessibilityPreferences.ThemeMode.SYSTEM
            )

            var localContrastMode by remember { mutableStateOf(contrastMode) }
            var localThemeMode by remember { mutableStateOf(themeMode) }

            GameBacklogTheme(
                contrastMode = localContrastMode,
                themeMode = localThemeMode
            ) {
                GameBacklogNavigation(
                    repository = repository,
                    accessibilityManager = accessibilityManager,
                    onThemeChange = { mode ->
                        localThemeMode = mode
                    },
                    onContrastChange = { mode ->
                        localContrastMode = mode
                    },
                    fontScaleFactor = fontScaleFactor
                )
            }
        }
    }
}