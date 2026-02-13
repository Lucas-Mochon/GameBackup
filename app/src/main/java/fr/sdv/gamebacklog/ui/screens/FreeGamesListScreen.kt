package fr.sdv.gamebacklog.ui.screens

import android.R.attr.contentDescription
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import fr.sdv.gamebacklog.data.remote.FreeGameResponse
import fr.sdv.gamebacklog.viewmodel.FreeGamesListViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.ui.semantics.contentDescription

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeGamesListScreen(
    onGameClick: (FreeGameResponse) -> Unit,
    fontScaleFactor: Float = 1f,
    onAddGameClick: () -> Unit,
) {
    val viewModel: FreeGamesListViewModel = viewModel()

    val games by viewModel.games.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val hasNextPage by viewModel.hasNextPage.collectAsState()

    var searchQuery by rememberSaveable { mutableStateOf("") }
    val normalizedQuery = searchQuery.trim().lowercase()
    val filteredGames = if (normalizedQuery.isBlank()) {
        games
    } else {
        games.filter { game ->
            game.title.lowercase().contains(normalizedQuery) ||
                game.platform.lowercase().contains(normalizedQuery) ||
                game.genre.lowercase().contains(normalizedQuery) ||
                game.developer.lowercase().contains(normalizedQuery)
        }
    }

    val listState = rememberLazyListState()

    LaunchedEffect(listState, normalizedQuery) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (normalizedQuery.isBlank() &&
                    lastVisibleIndex != null &&
                    lastVisibleIndex >= games.size - 3 &&
                    hasNextPage &&
                    !isLoading) {
                    viewModel.loadNextPage()
                }
            }
    }

    when {
        isLoading && games.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        error != null && games.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = error ?: "Erreur inconnue",
                    fontSize = (14.sp * fontScaleFactor),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        games.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Aucun jeu trouvé",
                    fontSize = (16.sp * fontScaleFactor)
                )
            }
        }

        else -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Liste des jeux",
                                fontSize = (18.sp * fontScaleFactor)
                            )
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = onAddGameClick,
                        modifier = Modifier.semantics {
                            contentDescription = "Ajouter un nouveau jeu"
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Rechercher un jeu") },
                    singleLine = true
                )

                if (filteredGames.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucun jeu ne correspond a votre recherche",
                            fontSize = (14.sp * fontScaleFactor)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        state = listState
                    ) {
                        itemsIndexed(
                            filteredGames,
                            key = { _, game -> "${game.id}-${game.title}" }
                        ) { _, game ->
                            FreeGameCard(
                                game = game,
                                onClick = { onGameClick(game) },
                                fontScaleFactor = fontScaleFactor
                            )
                        }

                        if (isLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        if (!hasNextPage && games.isNotEmpty() && normalizedQuery.isBlank()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Fin de la liste",
                                        fontSize = (12.sp * fontScaleFactor),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
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
        AsyncImage(
            model = game.thumbnail,
            contentDescription = game.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(8.dp)) {
            Row {
                Text(
                    text = game.title,
                    fontSize = (14.sp * fontScaleFactor),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(1f))
//                if (game.status != null {
//                    Text(
//                        text = "Favori",
//                        fontSize = (12.sp * fontScaleFactor),
//                        color = MaterialTheme.colorScheme.primary,
//                        modifier = Modifier.semantics {
//                            contentDescription = "Jeu favori"
//                        }
//                    )
//                } else {
//                    Text(
//                        text = "Non favori",
//                        fontSize = (12.sp * fontScaleFactor),
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        modifier = Modifier.semantics {
//                            contentDescription = "Jeu non favori"
//                        }
//                    )
//                }
            }

            Text(
                text = "${game.platform} • ${game.genre}",
                fontSize = (12.sp * fontScaleFactor),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
