package fr.sdv.gamebacklog.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import fr.sdv.gamebacklog.data.dao.GameDao
import fr.sdv.gamebacklog.data.model.Game
import fr.sdv.gamebacklog.data.seeder.GameSeeder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
                    .addCallback(DatabaseCallback())
                    .build()
                    .also { Instance = it }
            }
        }
    }

    // Callback pour peupler la base de données au premier lancement
    private class DatabaseCallback : RoomDatabase.Callback() {
        @OptIn(DelicateCoroutinesApi::class)
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Peupler la base avec les données d'exemple
            Instance?.let { database ->
                GlobalScope.launch {
                    try {
                        val gameDao = database.gameDao()
                        // Ajouter les jeux de seed
                        GameSeeder.getSampleGames().forEach { game ->
                            gameDao.insertGame(game)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}

