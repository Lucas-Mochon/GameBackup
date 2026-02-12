package fr.sdv.gamebacklog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.sdv.gamebacklog.data.model.Game
import fr.sdv.gamebacklog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEditGameViewModel(private val repository: GameRepository) : ViewModel() {

    private val _currentGame = MutableStateFlow<Game?>(null)
    val currentGame: StateFlow<Game?> = _currentGame.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _savingSuccess = MutableStateFlow(false)
    val savingSuccess: StateFlow<Boolean> = _savingSuccess.asStateFlow()

    fun loadGame(gameId: Int) = viewModelScope.launch {
        _isLoading.value = true
        val game = repository.getGameById(gameId)
        _currentGame.value = game
        _isLoading.value = false
    }

    fun saveGame(game: Game) = viewModelScope.launch {
        _isLoading.value = true
        try {
            if (game.id == 0) {
                repository.addGame(game)
            } else {
                repository.updateGame(game)
            }
            _savingSuccess.value = true
        } finally {
            _isLoading.value = false
        }
    }

    fun resetState() {
        _currentGame.value = null
        _savingSuccess.value = false
    }
}

class AddEditGameViewModelFactory(private val repository: GameRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddEditGameViewModel(repository) as T
    }
}

