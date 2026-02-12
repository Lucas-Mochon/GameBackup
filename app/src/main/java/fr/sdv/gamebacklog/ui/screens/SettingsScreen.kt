package fr.sdv.gamebacklog.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.sdv.gamebacklog.ui.components.AccessibleSlider
import fr.sdv.gamebacklog.utils.AccessibilityPreferences
import fr.sdv.gamebacklog.utils.AccessibilityPreferencesManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    accessibilityManager: AccessibilityPreferencesManager,
    onNavigateBack: () -> Unit,
    onThemeChange: (AccessibilityPreferences.ThemeMode) -> Unit,
    onContrastChange: (AccessibilityPreferences.ContrastMode) -> Unit,
    fontScaleFactor: Float = 1f
) {
    val scope = rememberCoroutineScope()

    val contrastMode by accessibilityManager.contrastMode.collectAsState(
        initial = AccessibilityPreferences.ContrastMode.NORMAL
    )
    val fontScale by accessibilityManager.fontScaleFactor.collectAsState(initial = 1.0f)
    val themeMode by accessibilityManager.themeMode.collectAsState(
        initial = AccessibilityPreferences.ThemeMode.SYSTEM
    )

    var highContrast by remember(contrastMode) {
        mutableStateOf(contrastMode == AccessibilityPreferences.ContrastMode.HIGH)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Paramètres d'accessibilité",
                        fontSize = (18.sp * fontScaleFactor)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.semantics {
                            contentDescription = "Retour"
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // High Contrast Toggle
            Text(
                text = "Contraste élevé",
                fontSize = (16.sp * fontScaleFactor),
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .semantics {
                        contentDescription = "Contraste élevé: ${if (highContrast) "Activé" else "Désactivé"}"
                    },
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = if (highContrast) "Activé" else "Désactivé",
                    fontSize = (14.sp * fontScaleFactor)
                )
                Switch(
                    checked = highContrast,
                    onCheckedChange = { isChecked ->
                        highContrast = isChecked
                        scope.launch {
                            val mode = if (isChecked) {
                                AccessibilityPreferences.ContrastMode.HIGH
                            } else {
                                AccessibilityPreferences.ContrastMode.NORMAL
                            }
                            accessibilityManager.setContrastMode(mode)
                            onContrastChange(mode)
                        }
                    }
                )
            }

            // Font Scale Slider
            AccessibleSlider(
                value = fontScale,
                onValueChange = { newScale ->
                    scope.launch {
                        accessibilityManager.setFontScaleFactor(newScale)
                    }
                },
                label = "Taille du texte (${(fontScale * 100).toInt()}%)",
                valueRange = 0.8f..2.0f,
                steps = 11,
                modifier = Modifier.padding(bottom = 24.dp),
                fontScaleFactor = fontScaleFactor
            )

            // Theme Mode selector
            Text(
                text = "Thème",
                fontSize = (16.sp * fontScaleFactor),
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            AccessibilityPreferences.ThemeMode.values().forEach { mode ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .semantics {
                            contentDescription = "Thème: ${mode.name}"
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = themeMode == mode,
                        onClick = {
                            scope.launch {
                                accessibilityManager.setThemeMode(mode)
                                onThemeChange(mode)
                            }
                        }
                    )
                    Text(
                        text = when (mode) {
                            AccessibilityPreferences.ThemeMode.LIGHT -> "Clair"
                            AccessibilityPreferences.ThemeMode.DARK -> "Sombre"
                            AccessibilityPreferences.ThemeMode.SYSTEM -> "Système"
                        },
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = (14.sp * fontScaleFactor)
                    )
                }
            }
        }
    }
}

