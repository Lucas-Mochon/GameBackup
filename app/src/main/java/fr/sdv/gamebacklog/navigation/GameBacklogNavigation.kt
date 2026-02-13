package fr.sdv.gamebacklog.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fr.sdv.gamebacklog.data.model.GameStatus
import fr.sdv.gamebacklog.data.repository.GameRepository
import fr.sdv.gamebacklog.ui.screens.AddEditGameScreen
import fr.sdv.gamebacklog.ui.screens.FreeGameDetailScreen
import fr.sdv.gamebacklog.ui.screens.GameListScreen
import fr.sdv.gamebacklog.ui.screens.GameStatusScreen
import fr.sdv.gamebacklog.ui.screens.SettingsScreen
import fr.sdv.gamebacklog.ui.screens.FreeGamesListScreen
import fr.sdv.gamebacklog.utils.AccessibilityPreferences
import fr.sdv.gamebacklog.utils.AccessibilityPreferencesManager
import fr.sdv.gamebacklog.viewmodel.AddEditGameViewModel
import fr.sdv.gamebacklog.viewmodel.AddEditGameViewModelFactory
import fr.sdv.gamebacklog.viewmodel.GameListViewModel
import fr.sdv.gamebacklog.viewmodel.GameListViewModelFactory
import fr.sdv.gamebacklog.viewmodel.FreeGameDetailViewModel

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun GameBacklogNavigation(
    repository: GameRepository,
    accessibilityManager: AccessibilityPreferencesManager,
    onThemeChange: (AccessibilityPreferences.ThemeMode) -> Unit,
    onContrastChange: (AccessibilityPreferences.ContrastMode) -> Unit,
    fontScaleFactor: Float = 1f
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Instance partagée du GameListViewModel
    val sharedGameListViewModel: GameListViewModel = viewModel(
        factory = GameListViewModelFactory(repository)
    )

    val bottomNavItems = listOf(
        BottomNavItem(GameBacklogScreen.FreeGamesList.route, "Découvrir", Icons.Default.Home),
        BottomNavItem(GameBacklogScreen.GameList.route, "À faire", Icons.Default.Favorite),
        BottomNavItem(GameBacklogScreen.InProgress.route, "En cours", Icons.Default.CheckCircle),
        BottomNavItem(GameBacklogScreen.Done.route, "Terminé", Icons.Default.CheckCircle),
        BottomNavItem(GameBacklogScreen.Settings.route, "⚙Paramètres", Icons.Default.Settings)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEachIndexed { _, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                restoreState = true
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = GameBacklogScreen.FreeGamesList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(GameBacklogScreen.FreeGamesList.route) {
                FreeGamesListScreen(
                    onGameClick = { freeGame ->
                        navController.navigate(
                            GameBacklogScreen.FreeGameDetail.createRoute(freeGame.id)
                        )
                    },
                    fontScaleFactor = fontScaleFactor
                )
            }


            composable(GameBacklogScreen.FreeGameDetail.route) { backStackEntry ->
                val gameId = backStackEntry.arguments?.getString("gameId")?.toIntOrNull() ?: 0
                val viewModel: FreeGameDetailViewModel = viewModel()

                FreeGameDetailScreen(
                    gameId = gameId,
                    viewModel = viewModel,
                    repository  = repository,
                    onGameAdded = {
                        navController.navigate(GameBacklogScreen.GameList.route) {
                            popUpTo(GameBacklogScreen.FreeGamesList.route) { inclusive = false }
                        }
                    },
                    onNavigateBack = {
                        navController.navigateUp()
                    },
                    fontScaleFactor = fontScaleFactor
                )
            }

            composable(GameBacklogScreen.GameList.route) {
                GameListScreen(
                    viewModel = sharedGameListViewModel,
                    status = null,
                    onGameClick = { game ->
                        navController.navigate("add_edit_game/${game.id}")
                    },
                    onAddGameClick = {
                        navController.navigate("add_edit_game/null")
                    },
                    onFreeGamesClick = {
                        navController.navigate(GameBacklogScreen.FreeGamesList.route) {
                            popUpTo(GameBacklogScreen.GameList.route) { inclusive = true }
                        }
                    },
                    fontScaleFactor = fontScaleFactor
                )
            }

            composable(GameBacklogScreen.InProgress.route) {
                GameStatusScreen(
                    viewModel = sharedGameListViewModel,
                    status = GameStatus.IN_PROGRESS,
                    onGameClick = { game ->
                        navController.navigate("add_edit_game/${game.id}")
                    },
                    onAddGameClick = {
                        navController.navigate("add_edit_game/null")
                    },
                    fontScaleFactor = fontScaleFactor
                )
            }

            composable(GameBacklogScreen.Done.route) {
                GameStatusScreen(
                    viewModel = sharedGameListViewModel,
                    status = GameStatus.DONE,
                    onGameClick = { game ->
                        navController.navigate("add_edit_game/${game.id}")
                    },
                    onAddGameClick = {
                        navController.navigate("add_edit_game/null")
                    },
                    fontScaleFactor = fontScaleFactor
                )
            }

            composable(GameBacklogScreen.Settings.route) {
                SettingsScreen(
                    accessibilityManager = accessibilityManager,
                    onNavigateBack = {
                        navController.navigateUp()
                    },
                    onThemeChange = onThemeChange,
                    onContrastChange = onContrastChange,
                    fontScaleFactor = fontScaleFactor
                )
            }

            composable(GameBacklogScreen.AddEditGame.route) { backStackEntry ->
                val gameIdStr = backStackEntry.arguments?.getString("gameId")
                val gameId = if (gameIdStr != "null" && gameIdStr != null) {
                    gameIdStr.toIntOrNull()
                } else {
                    null
                }

                val viewModel: AddEditGameViewModel = viewModel(
                    factory = AddEditGameViewModelFactory(repository)
                )

                // Charger le jeu chaque fois que l'ID change
                androidx.compose.runtime.LaunchedEffect(gameId) {
                    if (gameId != null && gameId != 0) {
                        viewModel.loadGame(gameId)
                    }
//                    } else {
//                        viewModel.resetState()
//                    }
                }

                AddEditGameScreen(
                    viewModel = viewModel,
                    onSave = {
                        Log.d("Navigation", "Current back stack:")
                        Log.d("Navigation", "Calling popBackStack")
                        navController.popBackStack()
                        Log.d("Navigation", "After popBackStack:")
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    fontScaleFactor = fontScaleFactor
                )
            }
        }
    }
}
