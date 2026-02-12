package fr.sdv.gamebacklog.data.repository

import fr.sdv.gamebacklog.data.dao.GameDao
import fr.sdv.gamebacklog.data.model.Game
import fr.sdv.gamebacklog.data.model.GameStatus
import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {

    fun getAllGames(): Flow<List<Game>> = gameDao.getAllGames()

    fun getGamesByStatus(status: GameStatus): Flow<List<Game>> =
        gameDao.getGamesByStatus(status)

    suspend fun getGameById(gameId: Int): Game? = gameDao.getGameById(gameId)

    suspend fun addGame(game: Game) = gameDao.insertGame(game)

    suspend fun updateGame(game: Game) = gameDao.updateGame(game)

    suspend fun deleteGame(game: Game) = gameDao.deleteGame(game)

    fun getGameCountByStatus(status: GameStatus): Flow<Int> =
        gameDao.getGameCountByStatus(status)
}

