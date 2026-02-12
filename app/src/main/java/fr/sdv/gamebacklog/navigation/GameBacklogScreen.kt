package fr.sdv.gamebacklog.navigation

sealed class GameBacklogScreen(val route: String) {
    object GameList : GameBacklogScreen("game_list")
    object InProgress : GameBacklogScreen("in_progress")
    object Done : GameBacklogScreen("done")
    object Settings : GameBacklogScreen("settings")
    object AddEditGame : GameBacklogScreen("add_edit_game/{gameId}") {
        fun createRoute(gameId: Int? = null) = "add_edit_game/${gameId ?: "null"}"
    }
    object FreeGamesList : GameBacklogScreen("free_games_list")
    object FreeGameDetail : GameBacklogScreen("free_game_detail/{gameId}") {
        fun createRoute(gameId: Int) = "free_game_detail/$gameId"
    }
}