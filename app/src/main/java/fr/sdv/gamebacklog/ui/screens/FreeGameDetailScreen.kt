package fr.sdv.gamebacklog.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import fr.sdv.gamebacklog.data.model.Game
import fr.sdv.gamebacklog.data.model.GameStatus
import fr.sdv.gamebacklog.data.repository.GameRepository
import fr.sdv.gamebacklog.ui.components.AccessibleButton
import fr.sdv.gamebacklog.ui.components.AccessibleSlider
import fr.sdv.gamebacklog.utils.ImageDownloadUtils
import fr.sdv.gamebacklog.viewmodel.FreeGameDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeGameDetailScreen(
    gameId: Int,
    viewModel: FreeGameDetailViewModel = FreeGameDetailViewModel(),
    repository: GameRepository,
    onGameAdded: () -> Unit,
    onNavigateBack: () -> Unit,
    fontScaleFactor: Float = 1f
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val gameDetail by viewModel.gameDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // 🔹 STATES UI (comme avant)
    var selectedStatus by remember { mutableStateOf(GameStatus.TO_DO) }
    var rating by remember { mutableStateOf(0) }
    var isAddingGame by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(gameId) {
        viewModel.loadGameDetail(gameId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "📖 Détail du jeu",
                        fontSize = (18.sp * fontScaleFactor),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
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
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                error != null || localError != null -> {
                    Text(
                        text = "${error ?: localError ?: "Erreur"}",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                gameDetail != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
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
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp)
                        )

                        Text(
                            text = "${gameDetail!!.platform} • ${gameDetail!!.genre}",
                            fontSize = (12.sp * fontScaleFactor),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Statut : ${selectedStatus.getLabel()}",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            GameStatus.values().forEach { status ->
                                AccessibleButton(
                                    text = status.getLabel(),
                                    onClick = { selectedStatus = status },
                                    modifier = Modifier.fillMaxWidth(),
                                    fontScaleFactor = fontScaleFactor
                                )
                            }
                        }

                        AccessibleSlider(
                            value = rating.toFloat(),
                            onValueChange = { rating = it.toInt() },
                            label = "Note : $rating / 10",
                            valueRange = 0f..10f,
                            steps = 9,
                            fontScaleFactor = fontScaleFactor
                        )

                        AccessibleButton(
                            text = if (isAddingGame) "Ajout en cours..." else "Ajouter à ma liste",
                            enabled = !isAddingGame,
                            modifier = Modifier.fillMaxWidth(),
                            fontScaleFactor = fontScaleFactor,
                            onClick = {
                                scope.launch {
                                    isAddingGame = true
                                    localError = null
                                    try {
                                        val localImagePath =
                                            ImageDownloadUtils.downloadAndSaveImage(
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
                                        onGameAdded()
                                    } catch (e: Exception) {
                                        localError = "Erreur ajout : ${e.message}"
                                    } finally {
                                        isAddingGame = false
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}