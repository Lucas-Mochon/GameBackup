package fr.sdv.gamebacklog.navigation

sealed class GameBacklogScreen(val route: String) {
    object GameList : GameBacklogScreen("game_list")
    object AddEditGame : GameBacklogScreen("add_edit_game/{gameId}") {
        fun createRoute(gameId: Int? = null) = "add_edit_game/${gameId ?: "null"}"
    }
    object Settings : GameBacklogScreen("settings")
    object Statistics : GameBacklogScreen("statistics")
}

