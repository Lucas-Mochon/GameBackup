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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.sdv.gamebacklog.data.model.Game
import fr.sdv.gamebacklog.data.model.GameStatus
import fr.sdv.gamebacklog.ui.components.AccessibleButton
import fr.sdv.gamebacklog.ui.components.AccessibleCard
import fr.sdv.gamebacklog.viewmodel.GameListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    viewModel: GameListViewModel,
    onGameClick: (Game) -> Unit,
    onAddGameClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    fontScaleFactor: Float = 1f
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val todoGames by viewModel.todoGames.collectAsState()
    val inProgressGames by viewModel.inProgressGames.collectAsState()
    val doneGames by viewModel.doneGames.collectAsState()

    val tabs = listOf(
        GameStatus.TO_DO to todoGames,
        GameStatus.IN_PROGRESS to inProgressGames,
        GameStatus.DONE to doneGames
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Game Backlog",
                        fontSize = (18.sp * fontScaleFactor)
                    )
                },
                actions = {
                    IconButton(
                        onClick = onStatisticsClick,
                        modifier = Modifier.semantics {
                            contentDescription = "Voir les statistiques"
                        }
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null)
                    }
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.semantics {
                            contentDescription = "Ouvrir les paramètres d'accessibilité"
                        }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    }
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
            // Tabs for filtering
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, (status, _) ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        modifier = Modifier.semantics {
                            contentDescription = "Filtrer par ${status.getLabel()}"
                        }
                    ) {
                        Text(
                            text = status.getLabel(),
                            modifier = Modifier.padding(16.dp),
                            fontSize = (14.sp * fontScaleFactor)
                        )
                    }
                }
            }

            // Games List
            val games = tabs[selectedTabIndex].second
            if (games.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun jeu dans cette catégorie",
                        fontSize = (16.sp * fontScaleFactor)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    items(games, key = { it.id }) { game ->
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
        // Game image thumbnail
        fr.sdv.gamebacklog.ui.components.GameImage(
            imagePath = game.imageUri,
            gameTitle = game.title,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        // Game info
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
                    text = "⏱️ ${game.hoursPlayed}h",
                    fontSize = (12.sp * fontScaleFactor)
                )
            }
            if (game.description.isNotEmpty()) {
                Text(
                    text = game.description.take(40) + if (game.description.length > 40) "..." else "",
                    fontSize = (11.sp * fontScaleFactor),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }

        // Delete button
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

