package fr.sdv.gamebacklog.viewmodel

import android.util.Log
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

    private val _hasNextPage = MutableStateFlow(true)
    val hasNextPage: StateFlow<Boolean> = _hasNextPage

    private val pageSize = 20
    private var allGamesCache: List<FreeGameResponse> = emptyList()
    private var isLoadingMore = false

    init {
        loadGames()
    }

    fun loadGames() {
        if (_isLoading.value) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _hasNextPage.value = true
            _games.value = emptyList()
            isLoadingMore = false

            try {
                allGamesCache = repository.getAllGames()

                val firstPage = allGamesCache.take(pageSize)

                _games.value = firstPage
                _hasNextPage.value = allGamesCache.size > firstPage.size

            } catch (e: Exception) {
                Log.e("FreeGamesVM", "ERREUR loadGames: ${e.message}", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadNextPage() {
        if (isLoadingMore) {
            return
        }

        if (!_hasNextPage.value) {
            return
        }

        isLoadingMore = true
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val currentSize = _games.value.size

                val nextPage = allGamesCache
                    .drop(currentSize)
                    .take(pageSize)

                if (nextPage.isNotEmpty()) {
                    val newList = _games.value + nextPage
                    _games.value = newList
                    _hasNextPage.value = allGamesCache.size > newList.size

                } else {
                    _hasNextPage.value = false
                }
            } catch (e: Exception) {
                Log.e("FreeGamesVM", "ERREUR loadNextPage: ${e.message}", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
                isLoadingMore = false
            }
        }
    }
}
