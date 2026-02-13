package fr.sdv.gamebacklog.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import fr.sdv.gamebacklog.viewmodel.AddEditGameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditGameScreen(
    viewModel: AddEditGameViewModel,
    onSave: () -> Unit,
    onNavigateBack: () -> Unit,
    fontScaleFactor: Float = 1f
) {
    val context = LocalContext.current
    val currentGame by viewModel.currentGame.collectAsState()

    var title by remember(currentGame) { mutableStateOf(currentGame?.title ?: "") }
    var platform by remember(currentGame) { mutableStateOf(currentGame?.platform ?: "") }
    var description by remember(currentGame) { mutableStateOf(currentGame?.description ?: "") }
    var releaseDate by remember(currentGame) { mutableStateOf(currentGame?.releaseDate ?: "") }
    var hoursPlayed by remember(currentGame) { mutableIntStateOf(currentGame?.hoursPlayed ?: 0) }
    var rating by remember(currentGame) { mutableIntStateOf(currentGame?.personalRating ?: 0) }
    var status by remember(currentGame) { mutableStateOf(currentGame?.status ?: GameStatus.TO_DO) }
    var imageUri by remember(currentGame) { mutableStateOf(currentGame?.imageUri ?: "") }

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
                        text = if (currentGame == null) "Ajouter un jeu" else "Modifier le jeu",
                        fontSize = (18.sp * fontScaleFactor),
                        fontWeight = FontWeight.Bold
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

            AccessibleButton(
                text = "📷 Choisir une image",
                onClick = { imageLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                fontScaleFactor = fontScaleFactor,
                icon = Icons.Default.MoreVert
            )

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
                ),
                singleLine = true
            )

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
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = releaseDate,
                onValueChange = { releaseDate = it },
                label = { Text("Date de sortie (YYYY-MM-DD)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .semantics { contentDescription = "Date de sortie" },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = (14.sp * fontScaleFactor)
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 12.dp)
                    .semantics { contentDescription = "Description" },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = (14.sp * fontScaleFactor)
                ),
                maxLines = 4
            )

            OutlinedTextField(
                value = hoursPlayed.toString(),
                onValueChange = { hoursPlayed = it.toIntOrNull() ?: 0 },
                label = { Text("Heures jouées") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .semantics { contentDescription = "Heures jouées" },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = (14.sp * fontScaleFactor)
                ),
                singleLine = true
            )

            AccessibleSlider(
                value = rating.toFloat(),
                onValueChange = { rating = it.toInt() },
                label = "Note personnelle: $rating/10",
                valueRange = 0f..10f,
                steps = 9,
                modifier = Modifier.padding(bottom = 16.dp),
                fontScaleFactor = fontScaleFactor
            )

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
                    fontScaleFactor = fontScaleFactor,
                    isSelected = status == gameStatus
                )
            }

            AccessibleButton(
                text = "Enregistrer",
                onClick = {
                    if (title.isNotBlank() && platform.isNotBlank()) {
                        val newGame = Game(
                            id = currentGame?.id ?: 0,
                            title = title,
                            platform = platform,
                            status = status,
                            personalRating = rating,
                            description = description,
                            imageUri = imageUri,
                            releaseDate = releaseDate,
                            hoursPlayed = hoursPlayed
                        )
                        viewModel.saveGame(newGame)
                        onSave()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                fontScaleFactor = fontScaleFactor
            )

            Text(
                text = "",
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}
