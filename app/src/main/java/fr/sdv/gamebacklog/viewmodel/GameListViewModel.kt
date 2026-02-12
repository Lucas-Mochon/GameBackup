package fr.sdv.gamebacklog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.sdv.gamebacklog.data.model.Game
import fr.sdv.gamebacklog.data.model.GameStatus
import fr.sdv.gamebacklog.data.repository.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameListViewModel(private val repository: GameRepository) : ViewModel() {

    val allGames: StateFlow<List<Game>> = repository.getAllGames()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val todoGames: StateFlow<List<Game>> = repository.getGamesByStatus(GameStatus.TO_DO)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val inProgressGames: StateFlow<List<Game>> = repository.getGamesByStatus(GameStatus.IN_PROGRESS)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val doneGames: StateFlow<List<Game>> = repository.getGamesByStatus(GameStatus.DONE)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun deleteGame(game: Game) = viewModelScope.launch {
        repository.deleteGame(game)
    }
}

class GameListViewModelFactory(private val repository: GameRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameListViewModel(repository) as T
    }
}

