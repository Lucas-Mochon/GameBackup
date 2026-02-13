package fr.sdv.gamebacklog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.sdv.gamebacklog.data.model.Game
import fr.sdv.gamebacklog.data.model.GameStatus
import fr.sdv.gamebacklog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameListViewModel(private val repository: GameRepository) : ViewModel() {

    private val _toDoGames = MutableStateFlow<List<Game>>(emptyList())

    private val _displayedToDoGames = MutableStateFlow<List<Game>>(emptyList())
    val displayedToDoGames: StateFlow<List<Game>> = _displayedToDoGames.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentPage = MutableStateFlow(0)

    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages: StateFlow<Boolean> = _hasMorePages.asStateFlow()

    private val pageSize = 20

    // StateFlows pour les autres statuts
    val inProgressGames: StateFlow<List<Game>> = repository
        .getGamesByStatus(GameStatus.IN_PROGRESS)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val doneGames: StateFlow<List<Game>> = repository
        .getGamesByStatus(GameStatus.DONE)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadToDoGames()
    }

    private fun loadToDoGames() = viewModelScope.launch {
        _isLoading.value = true
        repository.getGamesByStatus(GameStatus.TO_DO).collect { games ->
            _toDoGames.value = games
            _displayedToDoGames.value = emptyList()
            _currentPage.value = 0
            loadNextPage()
        }
    }

    fun loadNextPage() = viewModelScope.launch {
        val startIndex = _currentPage.value * pageSize
        val endIndex = minOf(startIndex + pageSize, _toDoGames.value.size)

        if (startIndex < _toDoGames.value.size) {
            val newGames = _toDoGames.value.subList(startIndex, endIndex)
            _displayedToDoGames.value = _displayedToDoGames.value + newGames
            _currentPage.value += 1
            _hasMorePages.value = endIndex < _toDoGames.value.size
        }
        _isLoading.value = false
    }

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
