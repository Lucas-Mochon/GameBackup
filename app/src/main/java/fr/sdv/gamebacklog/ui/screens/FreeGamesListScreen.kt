package fr.sdv.gamebacklog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import fr.sdv.gamebacklog.data.remote.FreeGameResponse
import fr.sdv.gamebacklog.viewmodel.FreeGamesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeGamesListScreen(
    viewModel: FreeGamesListViewModel = FreeGamesListViewModel(),
    onGameClick: (FreeGameResponse) -> Unit,
    onNavigateBack: () -> Unit,
    fontScaleFactor: Float = 1f
) {
    val games by viewModel.games.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🎮 Jeux Gratuits",
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
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.loadGames() },
                        modifier = Modifier.semantics {
                            contentDescription = "Rafraîchir les jeux"
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
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
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${error ?: "Erreur inconnue"}",
                            fontSize = (14.sp * fontScaleFactor),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                games.isEmpty() -> {
                    Text(
                        text = "Aucun jeu trouvé",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = (16.sp * fontScaleFactor)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(games, key = { it.id }) { game ->
                            FreeGameCard(
                                game = game,
                                onClick = { onGameClick(game) },
                                fontScaleFactor = fontScaleFactor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FreeGameCard(
    game: FreeGameResponse,
    onClick: () -> Unit,
    fontScaleFactor: Float = 1f
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        // Game thumbnail
        AsyncImage(
            model = game.thumbnail,
            contentDescription = "Image de ${game.title}",
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(8.dp))
                .semantics { contentDescription = "Couverture: ${game.title}" },
            contentScale = ContentScale.Crop
        )

        // Game info
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = game.title,
                fontSize = (14.sp * fontScaleFactor),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = " ${game.platform} • ${game.genre}",
                fontSize = (12.sp * fontScaleFactor),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = game.shortDescription,
                fontSize = (11.sp * fontScaleFactor),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = game.developer,
                fontSize = (10.sp * fontScaleFactor),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = game.releaseDate,
                fontSize = (10.sp * fontScaleFactor),
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

