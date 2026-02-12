package fr.sdv.gamebacklog.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.sdv.gamebacklog.data.remote.FreeGameDetailResponse
import fr.sdv.gamebacklog.data.repository.FreeGamesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FreeGameDetailViewModel(
    private val repository: FreeGamesRepository = FreeGamesRepository()
) : ViewModel() {

    private val _gameDetail = MutableStateFlow<FreeGameDetailResponse?>(null)
    val gameDetail: StateFlow<FreeGameDetailResponse?> = _gameDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadGameDetail(gameId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val detail = repository.getGameById(gameId)

                if (detail != null) {
                    _gameDetail.value = detail
                } else {
                    _error.value = "Jeu non trouvé"
                }
            } catch (e: Exception) {
                _error.value = "Erreur: ${e.message}"
                Log.e("GameDetailVM", "Erreur chargement détail", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}