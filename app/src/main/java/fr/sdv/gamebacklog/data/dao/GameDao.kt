package fr.sdv.gamebacklog.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import fr.sdv.gamebacklog.data.model.Game
import fr.sdv.gamebacklog.data.model.GameStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM games ORDER BY status, title")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE status = :status ORDER BY title")
    fun getGamesByStatus(status: GameStatus): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE id = :gameId")
    suspend fun getGameById(gameId: Int): Game?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game)

    @Update
    suspend fun updateGame(game: Game)

    @Delete
    suspend fun deleteGame(game: Game)

    @Query("SELECT COUNT(*) FROM games WHERE status = :status")
    fun getGameCountByStatus(status: GameStatus): Flow<Int>
}

