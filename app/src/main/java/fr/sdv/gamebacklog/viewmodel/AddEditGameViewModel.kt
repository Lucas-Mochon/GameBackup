package fr.sdv.gamebacklog.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.sdv.gamebacklog.data.model.Game
import fr.sdv.gamebacklog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

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
        Log.d("AddEditGameViewModel", "saveGame called with game: ${game.title}, id: ${game.id}, status: ${game.status}")
        try {
            if (game.id == 0) {
                val uniqueId = Math.abs(UUID.randomUUID().hashCode())
                val gameWithId = game.copy(id = uniqueId)
                Log.d("AddEditGameViewModel", "Inserting new game with generated id: $uniqueId")
                repository.addGame(gameWithId)
                Log.d("AddEditGameViewModel", "Game inserted successfully")
            } else {
                Log.d("AddEditGameViewModel", "Updating existing game with id: ${game.id}")
                repository.updateGame(game)
                Log.d("AddEditGameViewModel", "Game updated successfully")
            }
            _savingSuccess.value = true
            Log.d("AddEditGameViewModel", "savingSuccess set to true")
        } catch (e: Exception) {
            Log.e("AddEditGameViewModel", "Error saving game", e)
        } finally {
            _isLoading.value = false
        }
    }

    fun resetState() {
        Log.d("AddEditGameViewModel", "resetState called")
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

