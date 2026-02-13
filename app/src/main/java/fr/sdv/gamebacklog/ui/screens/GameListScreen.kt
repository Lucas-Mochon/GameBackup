package fr.sdv.gamebacklog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.sdv.gamebacklog.data.model.Game
import fr.sdv.gamebacklog.data.model.GameStatus
import fr.sdv.gamebacklog.ui.components.GameImage
import fr.sdv.gamebacklog.viewmodel.GameListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    viewModel: GameListViewModel,
//    status: GameStatus? = null,
    onGameClick: (Game) -> Unit,
    onAddGameClick: () -> Unit,
//    onFreeGamesClick: () -> Unit,
    fontScaleFactor: Float = 1f
) {
    val displayedGames by viewModel.displayedToDoGames.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasMorePages by viewModel.hasMorePages.collectAsState()
    val lazyListState = rememberLazyListState()

    // Détection du scroll pour charger plus
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleIndex = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex >= displayedGames.size - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && hasMorePages && !isLoading) {
            viewModel.loadNextPage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "À faire",
                        fontSize = (18.sp * fontScaleFactor)
                    )
                }
            )
        },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = onAddGameClick,
//                modifier = Modifier.semantics {
//                    contentDescription = "Ajouter un nouveau jeu"
//                }
//            ) {
//                Icon(Icons.Default.Add, contentDescription = null)
//            }
//        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (displayedGames.isEmpty() && !isLoading) {
                Text(
                    text = "Aucun jeu \nVous pouvez en ajouter depuis la page d'accueil",
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    fontSize = (16.sp * fontScaleFactor)
                )
            } else {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        displayedGames,
                        key = { index, game -> "${game.id}-$index" }
                    ) { _, game ->
                        GameListItem(
                            game = game,
                            onGameClick = { onGameClick(game) },
                            onDeleteClick = { viewModel.deleteGame(game) },
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
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameStatusScreen(
    viewModel: GameListViewModel,
    status: GameStatus,
    onGameClick: (Game) -> Unit,
    onAddGameClick: () -> Unit,
    fontScaleFactor: Float = 1f
) {
    // Utiliser le bon StateFlow selon le statut
    val games by when (status) {
        GameStatus.IN_PROGRESS -> viewModel.inProgressGames.collectAsState()
        GameStatus.DONE -> viewModel.doneGames.collectAsState()
        else -> viewModel.displayedToDoGames.collectAsState()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = status.getLabel(),
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (games.isEmpty()) {
                Text(
                    text = "Aucun jeu",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = (16.sp * fontScaleFactor)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        games,
                        key = { _, game -> game.id }  //
                    ) { _, game ->
                        GameListItem(
                            game = game,
                            onGameClick = { onGameClick(game) },
                            onDeleteClick = { viewModel.deleteGame(game) },
                            fontScaleFactor = fontScaleFactor
                        )
                    }
                }
            }
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun GameStatusScreen(
//    viewModel: GameListViewModel,
//    status: GameStatus,
//    onGameClick: (Game) -> Unit,
//    onAddGameClick: () -> Unit,
//    fontScaleFactor: Float = 1f
//) {
//    val games by viewModel.getGamesByStatus(status).collectAsState()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = status.getLabel(),
//                        fontSize = (18.sp * fontScaleFactor)
//                    )
//                }
//            )
//        },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = onAddGameClick,
//                modifier = Modifier.semantics {
//                    contentDescription = "Ajouter un nouveau jeu"
//                }
//            ) {
//                Icon(Icons.Default.Add, contentDescription = null)
//            }
//        }
//    ) { innerPadding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//        ) {
//            if (games.isEmpty()) {
//                Text(
//                    text = "Aucun jeu",
//                    modifier = Modifier.align(Alignment.Center),
//                    fontSize = (16.sp * fontScaleFactor)
//                )
//            } else {
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    itemsIndexed(
//                        games,
//                        key = { index, game -> "${game.id}-$index" }
//                    ) { _, game ->
//                        GameListItem(
//                            game = game,
//                            onGameClick = { onGameClick(game) },
//                            onDeleteClick = { viewModel.deleteGame(game) },
//                            fontScaleFactor = fontScaleFactor
//                        )
//                    }
//                }
//            }
//        }
//    }
//}

@Composable
fun GameListItem(
    game: Game,
    onGameClick: () -> Unit,
    onDeleteClick: () -> Unit,
    fontScaleFactor: Float = 1f
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onGameClick)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GameImage(
            imagePath = game.imageUri,
            gameTitle = game.title,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            Text(
                text = game.title,
                fontSize = (14.sp * fontScaleFactor),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = game.platform,
                fontSize = (12.sp * fontScaleFactor),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "⭐ ${game.personalRating}/10",
                    fontSize = (12.sp * fontScaleFactor)
                )
                Text(
                    text = "⏱ ${game.hoursPlayed}h",
                    fontSize = (12.sp * fontScaleFactor)
                )
            }
        }

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.semantics {
                contentDescription = "Supprimer ${game.title}"
            }
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}
