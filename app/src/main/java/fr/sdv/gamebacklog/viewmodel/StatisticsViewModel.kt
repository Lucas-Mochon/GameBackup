package fr.sdv.gamebacklog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.sdv.gamebacklog.data.model.GameStatus
import fr.sdv.gamebacklog.data.repository.GameRepository
import kotlinx.coroutines.flow.Flow

class StatisticsViewModel(private val repository: GameRepository) : ViewModel() {

    val todoCount: Flow<Int> = repository.getGameCountByStatus(GameStatus.TO_DO)
    val inProgressCount: Flow<Int> = repository.getGameCountByStatus(GameStatus.IN_PROGRESS)
    val doneCount: Flow<Int> = repository.getGameCountByStatus(GameStatus.DONE)
}

class StatisticsViewModelFactory(private val repository: GameRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StatisticsViewModel(repository) as T
    }
}

