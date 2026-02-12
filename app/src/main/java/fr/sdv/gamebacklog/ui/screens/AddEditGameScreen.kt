package fr.sdv.gamebacklog.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.sdv.gamebacklog.data.model.Game
import fr.sdv.gamebacklog.data.model.GameStatus
import fr.sdv.gamebacklog.ui.components.AccessibleButton
import fr.sdv.gamebacklog.ui.components.AccessibleSlider
import fr.sdv.gamebacklog.ui.components.GameImage
import fr.sdv.gamebacklog.utils.ImageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditGameScreen(
    game: Game? = null,
    onSave: (Game) -> Unit,
    onNavigateBack: () -> Unit,
    fontScaleFactor: Float = 1f
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(game?.title ?: "") }
    var platform by remember { mutableStateOf(game?.platform ?: "") }
    var description by remember { mutableStateOf(game?.description ?: "") }
    var releaseDate by remember { mutableStateOf(game?.releaseDate ?: "") }
    var hoursPlayed by remember { mutableIntStateOf(game?.hoursPlayed ?: 0) }
    var rating by remember { mutableIntStateOf(game?.personalRating ?: 0) }
    var status by remember { mutableStateOf(game?.status ?: GameStatus.TO_DO) }
    var imageUri by remember { mutableStateOf(game?.imageUri ?: "") }

    // Image picker launcher
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = ImageUtils.saveImageFromUri(context, it)
            if (savedPath != null) {
                imageUri = savedPath
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (game == null) "Ajouter un jeu" else "Modifier le jeu",
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
            // Game image display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                GameImage(
                    imagePath = imageUri,
                    gameTitle = title,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Image picker button
            AccessibleButton(
                text = "📷 Choisir une image",
                onClick = { imageLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                fontScaleFactor = fontScaleFactor,
                icon = Icons.Default.Search
            )

            // Title field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titre du jeu *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .semantics { contentDescription = "Titre du jeu" },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = (14.sp * fontScaleFactor)
                )
            )

            // Platform field
            OutlinedTextField(
                value = platform,
                onValueChange = { platform = it },
                label = { Text("Plateforme (PS5, Xbox, PC...) *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .semantics { contentDescription = "Plateforme" },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = (14.sp * fontScaleFactor)
                )
            )

            // Release date
            OutlinedTextField(
                value = releaseDate,
                onValueChange = { releaseDate = it },
                label = { Text("Date de sortie (YYYY-MM-DD)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = (14.sp * fontScaleFactor)
                )
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 12.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = (14.sp * fontScaleFactor)
                ),
                maxLines = 4
            )

            // Hours played
            OutlinedTextField(
                value = hoursPlayed.toString(),
                onValueChange = { hoursPlayed = it.toIntOrNull() ?: 0 },
                label = { Text("Heures jouées") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = (14.sp * fontScaleFactor)
                )
            )

            // Rating slider
            AccessibleSlider(
                value = rating.toFloat(),
                onValueChange = { rating = it.toInt() },
                label = "Note personnelle: $rating/10",
                valueRange = 0f..10f,
                steps = 9,
                modifier = Modifier.padding(bottom = 16.dp),
                fontScaleFactor = fontScaleFactor
            )

            // Status selector
            Text(
                text = "Statut: ${status.getLabel()}",
                fontSize = (14.sp * fontScaleFactor),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            GameStatus.values().forEach { gameStatus ->
                AccessibleButton(
                    text = gameStatus.getLabel(),
                    onClick = { status = gameStatus },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    fontScaleFactor = fontScaleFactor
                )
            }

            // Save button
            AccessibleButton(
                text = "✅ Enregistrer",
                onClick = {
                    if (title.isNotBlank() && platform.isNotBlank()) {
                        val newGame = Game(
                            id = game?.id ?: 0,
                            title = title,
                            platform = platform,
                            status = status,
                            personalRating = rating,
                            description = description,
                            imageUri = imageUri,
                            releaseDate = releaseDate,
                            hoursPlayed = hoursPlayed
                        )
                        onSave(newGame)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                fontScaleFactor = fontScaleFactor
            )
        }
    }
}


