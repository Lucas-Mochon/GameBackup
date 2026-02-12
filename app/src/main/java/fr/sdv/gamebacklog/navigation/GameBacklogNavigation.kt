package fr.sdv.gamebacklog.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.sdv.gamebacklog.data.repository.GameRepository
import fr.sdv.gamebacklog.ui.screens.AddEditGameScreen
import fr.sdv.gamebacklog.ui.screens.GameListScreen
import fr.sdv.gamebacklog.ui.screens.SettingsScreen
import fr.sdv.gamebacklog.ui.screens.StatisticsScreen
import fr.sdv.gamebacklog.utils.AccessibilityPreferences
import fr.sdv.gamebacklog.utils.AccessibilityPreferencesManager
import fr.sdv.gamebacklog.viewmodel.AddEditGameViewModel
import fr.sdv.gamebacklog.viewmodel.AddEditGameViewModelFactory
import fr.sdv.gamebacklog.viewmodel.GameListViewModel
import fr.sdv.gamebacklog.viewmodel.GameListViewModelFactory
import fr.sdv.gamebacklog.viewmodel.StatisticsViewModel
import fr.sdv.gamebacklog.viewmodel.StatisticsViewModelFactory

@Composable
fun GameBacklogNavigation(
    repository: GameRepository,
    accessibilityManager: AccessibilityPreferencesManager,
    onThemeChange: (AccessibilityPreferences.ThemeMode) -> Unit,
    onContrastChange: (AccessibilityPreferences.ContrastMode) -> Unit,
    fontScaleFactor: Float = 1f
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = GameBacklogScreen.GameList.route
    ) {
        composable(GameBacklogScreen.GameList.route) {
            val viewModel: GameListViewModel = viewModel(
                factory = GameListViewModelFactory(repository)
            )
            GameListScreen(
                viewModel = viewModel,
                onGameClick = { game ->
                    navController.navigate("add_edit_game/${game.id}")
                },
                onAddGameClick = {
                    navController.navigate("add_edit_game/null")
                },
                onSettingsClick = {
                    navController.navigate(GameBacklogScreen.Settings.route)
                },
                onStatisticsClick = {
                    navController.navigate(GameBacklogScreen.Statistics.route)
                },
                fontScaleFactor = fontScaleFactor
            )
        }

        composable(GameBacklogScreen.AddEditGame.route) { backStackEntry ->
            val gameIdStr = backStackEntry.arguments?.getString("gameId")
            val gameId = if (gameIdStr != "null") gameIdStr?.toIntOrNull() else null

            val viewModel: AddEditGameViewModel = viewModel(
                factory = AddEditGameViewModelFactory(repository)
            )

            val currentGame by viewModel.currentGame.collectAsState()

            if (gameId != null) {
                viewModel.loadGame(gameId)
            }

            AddEditGameScreen(
                game = currentGame,
                onSave = { game ->
                    viewModel.saveGame(game)
                    navController.navigateUp()
                },
                onNavigateBack = {
                    navController.navigateUp()
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

        composable(GameBacklogScreen.Statistics.route) {
            val viewModel: StatisticsViewModel = viewModel(
                factory = StatisticsViewModelFactory(repository)
            )
            StatisticsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.navigateUp()
                },
                fontScaleFactor = fontScaleFactor
            )
        }
    }
}

