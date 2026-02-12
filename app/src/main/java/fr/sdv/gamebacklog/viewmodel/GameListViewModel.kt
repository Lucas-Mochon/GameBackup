package fr.sdv.gamebacklog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.sdv.gamebacklog.data.model.Game
import fr.sdv.gamebacklog.data.model.GameStatus
import fr.sdv.gamebacklog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameListViewModel(private val repository: GameRepository) : ViewModel() {

    private val _allGames = MutableStateFlow<List<Game>>(emptyList())
    val allGames: StateFlow<List<Game>> = _allGames.asStateFlow()

    private val _displayedGames = MutableStateFlow<List<Game>>(emptyList())
    val displayedGames: StateFlow<List<Game>> = _displayedGames.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages: StateFlow<Boolean> = _hasMorePages.asStateFlow()

    private val pageSize = 20

    init {
        loadAllGames()
    }

    private fun loadAllGames() = viewModelScope.launch {
        _isLoading.value = true
        repository.getAllGames().collect { games ->
            _allGames.value = games
            _currentPage.value = 0
            loadNextPage()
        }
    }

    fun loadNextPage() = viewModelScope.launch {
        val startIndex = _currentPage.value * pageSize
        val endIndex = minOf(startIndex + pageSize, _allGames.value.size)

        if (startIndex < _allGames.value.size) {
            val newGames = _allGames.value.subList(startIndex, endIndex)
            _displayedGames.value = _displayedGames.value + newGames
            _currentPage.value += 1
            _hasMorePages.value = endIndex < _allGames.value.size
        }
        _isLoading.value = false
    }

    fun getGamesByStatus(status: GameStatus): StateFlow<List<Game>> {
        val flow = MutableStateFlow<List<Game>>(emptyList())
        viewModelScope.launch {
            repository.getGamesByStatus(status).collect { games ->
                flow.value = games
            }
        }
        return flow.asStateFlow()
    }

    fun deleteGame(game: Game) = viewModelScope.launch {
        repository.deleteGame(game)
    }

    fun refreshGames() {
        _displayedGames.value = emptyList()
        _currentPage.value = 0
        loadAllGames()
    }
}

class GameListViewModelFactory(private val repository: GameRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameListViewModel(repository) as T
    }
}
