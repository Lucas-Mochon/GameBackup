package fr.sdv.gamebacklog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.sdv.gamebacklog.data.remote.FreeGameResponse
import fr.sdv.gamebacklog.data.repository.FreeGamesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FreeGamesListViewModel(
    private val repository: FreeGamesRepository = FreeGamesRepository()
) : ViewModel() {

    private val _games = MutableStateFlow<List<FreeGameResponse>>(emptyList())
    val games: StateFlow<List<FreeGameResponse>> = _games

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadGames()
    }

    fun loadGames() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val gamesList = repository.getAllGames()
                _games.value = gamesList
            } catch (e: Exception) {
                _error.value = "Erreur lors du chargement des jeux: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByPlatform(platform: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val gamesList = repository.getGamesByPlatform(platform)
                _games.value = gamesList
            } catch (e: Exception) {
                _error.value = "Erreur: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val gamesList = repository.getGamesByCategory(category)
                _games.value = gamesList
            } catch (e: Exception) {
                _error.value = "Erreur: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sortBy(sortBy: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val gamesList = repository.getGamesSorted(sortBy)
                _games.value = gamesList
            } catch (e: Exception) {
                _error.value = "Erreur: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

