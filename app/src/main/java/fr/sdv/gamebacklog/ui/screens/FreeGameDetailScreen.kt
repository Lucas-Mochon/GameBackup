package fr.sdv.gamebacklog.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import coil.compose.AsyncImage
import fr.sdv.gamebacklog.data.model.Game
import fr.sdv.gamebacklog.data.model.GameStatus
import fr.sdv.gamebacklog.data.repository.GameRepository
import fr.sdv.gamebacklog.data.repository.FreeGamesRepository
import fr.sdv.gamebacklog.ui.components.AccessibleButton
import fr.sdv.gamebacklog.ui.components.AccessibleSlider
import fr.sdv.gamebacklog.utils.ImageDownloadUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeGameDetailScreen(
    gameId: Int,
    repository: GameRepository,
    onGameAdded: () -> Unit,
    onNavigateBack: () -> Unit,
    fontScaleFactor: Float = 1f
) {
    val context = LocalContext.current
    val freeGamesRepository = remember { FreeGamesRepository() }
    val scope = rememberCoroutineScope()

    var gameDetail by remember { mutableStateOf<fr.sdv.gamebacklog.data.remote.FreeGameDetailResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isAddingGame by remember { mutableStateOf(false) }

    var selectedStatus by remember { mutableStateOf(GameStatus.TO_DO) }
    var rating by remember { mutableStateOf(0) }

    LaunchedEffect(gameId) {
        scope.launch {
            try {
                gameDetail = freeGamesRepository.getGameById(gameId)
                isLoading = false
            } catch (e: Exception) {
                error = "Erreur: ${e.message}"
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ajouter un jeu",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Text(
                        text = error ?: "Erreur",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                gameDetail != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AsyncImage(
                            model = gameDetail!!.thumbnail,
                            contentDescription = gameDetail!!.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = gameDetail!!.title,
                            fontSize = (20.sp * fontScaleFactor),
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Plateforme: ${gameDetail!!.platform}",
                            fontSize = (14.sp * fontScaleFactor)
                        )
                        Text(
                            text = "Genre: ${gameDetail!!.genre}",
                            fontSize = (14.sp * fontScaleFactor)
                        )
                        Text(
                            text = "Développeur: ${gameDetail!!.developer}",
                            fontSize = (14.sp * fontScaleFactor)
                        )
                        Text(
                            text = "Éditeur: ${gameDetail!!.publisher}",
                            fontSize = (14.sp * fontScaleFactor)
                        )
                        Text(
                            text = "Date de sortie: ${gameDetail!!.releaseDate}",
                            fontSize = (14.sp * fontScaleFactor)
                        )

                        Text(
                            text = "Description",
                            fontSize = (16.sp * fontScaleFactor),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = gameDetail!!.description,
                            fontSize = (13.sp * fontScaleFactor)
                        )

                        Text(
                            text = "Statut: ${selectedStatus.getLabel()}",
                            fontSize = (14.sp * fontScaleFactor),
                            fontWeight = FontWeight.Bold
                        )
                        GameStatus.values().forEach { status ->
                            AccessibleButton(
                                text = status.getLabel(),
                                onClick = { selectedStatus = status },
                                modifier = Modifier.fillMaxWidth(),
                                fontScaleFactor = fontScaleFactor
                            )
                        }

                        AccessibleSlider(
                            value = rating.toFloat(),
                            onValueChange = { rating = it.toInt() },
                            label = "Note: $rating/10",
                            valueRange = 0f..10f,
                            steps = 9,
                            fontScaleFactor = fontScaleFactor
                        )

                        AccessibleButton(
                            text = if (isAddingGame) "Ajout en cours..." else "Ajouter à ma liste",
                            onClick = {
                                scope.launch {
                                    isAddingGame = true
                                    try {
                                        val localImagePath = ImageDownloadUtils.downloadAndSaveImage(
                                            context,
                                            gameDetail!!.thumbnail,
                                            "game_${gameDetail!!.id}.jpg"
                                        )

                                        val newGame = Game(
                                            title = gameDetail!!.title,
                                            platform = gameDetail!!.platform,
                                            status = selectedStatus,
                                            personalRating = rating,
                                            description = gameDetail!!.description,
                                            releaseDate = gameDetail!!.releaseDate,
                                            imageUri = localImagePath ?: ""
                                        )
                                        repository.addGame(newGame)
                                        isAddingGame = false
                                        onGameAdded()
                                    } catch (e: Exception) {
                                        error = "Erreur lors de l'ajout: ${e.message}"
                                        isAddingGame = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            fontScaleFactor = fontScaleFactor,
                            enabled = !isAddingGame
                        )

                        Text(text = "", modifier = Modifier.padding(bottom = 24.dp))
                    }
                }
            }
        }
    }
}
