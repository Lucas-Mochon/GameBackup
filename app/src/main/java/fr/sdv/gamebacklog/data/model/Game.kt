package fr.sdv.gamebacklog.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val platform: String,
    val status: GameStatus = GameStatus.TO_DO,
    val personalRating: Int = 0,
    val description: String = "",
    val imageUri: String = "",
    val releaseDate: String = "",
    val hoursPlayed: Int = 0
)

