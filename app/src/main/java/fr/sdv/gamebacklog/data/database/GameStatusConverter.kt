package fr.sdv.gamebacklog.data.database

import androidx.room.TypeConverter
import fr.sdv.gamebacklog.data.model.GameStatus

class GameStatusConverter {

    @TypeConverter
    fun fromGameStatus(status: GameStatus): String = status.name

    @TypeConverter
    fun toGameStatus(name: String): GameStatus = GameStatus.valueOf(name)
}

