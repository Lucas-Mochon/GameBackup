package fr.sdv.gamebacklog.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.sdv.gamebacklog.data.dao.GameDao
import fr.sdv.gamebacklog.data.model.Game

@Database(
    entities = [Game::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(GameStatusConverter::class)
abstract class GameBacklogDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var Instance: GameBacklogDatabase? = null

        fun getDatabase(context: Context): GameBacklogDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    GameBacklogDatabase::class.java,
                    "gamebacklog_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

