package fr.sdv.gamebacklog.viewmodel

import androidx.lifecycle.viewModelScope
import fr.sdv.gamebacklog.data.model.Game
import fr.sdv.gamebacklog.data.model.GameStatus
import fr.sdv.gamebacklog.data.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Exemple de tests unitaires pour GameListViewModel
 *
 * Pour utiliser :
 * 1. Ajouter les dépendances test dans build.gradle.kts
 * 2. Créer des tests similaires pour les autres ViewModels
 */
class GameListViewModelTest {

    @Mock
    private lateinit var mockRepository: GameRepository

    private lateinit var viewModel: GameListViewModel

    private val testGame = Game(
        id = 1,
        title = "Elden Ring",
        platform = "PS5",
        status = GameStatus.IN_PROGRESS,
        personalRating = 9
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = GameListViewModel(mockRepository)
    }

    @Test
    fun `deleteGame should call repository deleteGame`() = runTest {
        // When
        viewModel.deleteGame(testGame)

        // Then
        verify(mockRepository).deleteGame(testGame)
    }

    @Test
    fun `todoGames should emit todo games from repository`() = runTest {
        // Given
        val todoGames = listOf(
            Game(id = 1, title = "Game 1", platform = "PS5", status = GameStatus.TO_DO, personalRating = 7),
            Game(id = 2, title = "Game 2", platform = "Xbox", status = GameStatus.TO_DO, personalRating = 8)
        )

        whenever(mockRepository.getGamesByStatus(GameStatus.TO_DO))
            .thenReturn(flow { emit(todoGames) })

        // Then
        // Vérifier que le flow émet les bonnes données
        // (Nécessite des tests Flow plus avancés)
    }

    @Test
    fun `allGames should combine all game statuses`() = runTest {
        // Given
        val allGames = listOf(
            Game(id = 1, title = "Game 1", platform = "PS5", status = GameStatus.TO_DO, personalRating = 7),
            Game(id = 2, title = "Game 2", platform = "Xbox", status = GameStatus.IN_PROGRESS, personalRating = 8),
            Game(id = 3, title = "Game 3", platform = "PC", status = GameStatus.DONE, personalRating = 9)
        )

        whenever(mockRepository.getAllGames())
            .thenReturn(flow { emit(allGames) })

        // Then
        // Vérifier que le flow émet toutes les données
    }
}

/**
 * Exemple de tests pour GameRepository avec mock DAO
 */
class GameRepositoryTest {

    @Mock
    private lateinit var mockDao: fr.sdv.gamebacklog.data.dao.GameDao

    private lateinit var repository: GameRepository

    private val testGame = Game(
        id = 1,
        title = "Elden Ring",
        platform = "PS5",
        status = GameStatus.IN_PROGRESS,
        personalRating = 9
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = GameRepository(mockDao)
    }

    @Test
    fun `addGame should call dao insertGame`() = runTest {
        // When
        repository.addGame(testGame)

        // Then
        verify(mockDao).insertGame(testGame)
    }

    @Test
    fun `updateGame should call dao updateGame`() = runTest {
        // When
        repository.updateGame(testGame)

        // Then
        verify(mockDao).updateGame(testGame)
    }

    @Test
    fun `deleteGame should call dao deleteGame`() = runTest {
        // When
        repository.deleteGame(testGame)

        // Then
        verify(mockDao).deleteGame(testGame)
    }

    @Test
    fun `getAllGames should return dao flow`() = runTest {
        // Given
        val games = listOf(testGame)
        whenever(mockDao.getAllGames())
            .thenReturn(flow { emit(games) })

        // When
        val result = repository.getAllGames()

        // Then
        verify(mockDao).getAllGames()
    }

    @Test
    fun `getGamesByStatus should call dao with correct status`() = runTest {
        // Given
        val games = listOf(testGame)
        whenever(mockDao.getGamesByStatus(GameStatus.IN_PROGRESS))
            .thenReturn(flow { emit(games) })

        // When
        val result = repository.getGamesByStatus(GameStatus.IN_PROGRESS)

        // Then
        verify(mockDao).getGamesByStatus(GameStatus.IN_PROGRESS)
    }
}

/**
 * Pour utiliser ces tests :
 *
 * 1. Ajouter dans build.gradle.kts :
 *
 * testImplementation "junit:junit:4.13.2"
 * testImplementation "org.mockito:mockito-core:5.0.0"
 * testImplementation "org.mockito.kotlin:mockito-kotlin:5.0.0"
 * testImplementation "androidx.arch.core:core-testing:2.2.0"
 * testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3"
 *
 * 2. Créer les tests dans src/test/java/
 * 3. Lancer avec : ./gradlew test
 */

