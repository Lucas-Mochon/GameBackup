package fr.sdv.gamebacklog.data.seeder

import fr.sdv.gamebacklog.data.model.Game
import fr.sdv.gamebacklog.data.model.GameStatus

object GameSeeder {
    fun getSampleGames(): List<Game> = listOf(
        Game(
            title = "Elden Ring",
            platform = "PS5",
            status = GameStatus.IN_PROGRESS,
            personalRating = 9,
            description = "Epic action RPG from FromSoftware. Challenging but amazing.",
            releaseDate = "2022-02-25",
            hoursPlayed = 45
        ),
        Game(
            title = "Tears of the Kingdom",
            platform = "Nintendo Switch",
            status = GameStatus.DONE,
            personalRating = 10,
            description = "Sequel to BOTW. Open-world masterpiece.",
            releaseDate = "2023-05-12",
            hoursPlayed = 120
        ),
        Game(
            title = "Baldur's Gate 3",
            platform = "PC",
            status = GameStatus.IN_PROGRESS,
            personalRating = 9,
            description = "Deep RPG with incredible story choices.",
            releaseDate = "2023-08-03",
            hoursPlayed = 80
        ),
        Game(
            title = "Starfield",
            platform = "Xbox Series X",
            status = GameStatus.TO_DO,
            personalRating = 0,
            description = "Space exploration game from Bethesda.",
            releaseDate = "2023-09-06",
            hoursPlayed = 0
        ),
        Game(
            title = "Final Fantasy XVI",
            platform = "PS5",
            status = GameStatus.DONE,
            personalRating = 8,
            description = "Latest FF with action combat.",
            releaseDate = "2023-06-22",
            hoursPlayed = 60
        ),
        Game(
            title = "Cyberpunk 2077",
            platform = "PC",
            status = GameStatus.IN_PROGRESS,
            personalRating = 8,
            description = "Enhanced version is much better now.",
            releaseDate = "2020-12-10",
            hoursPlayed = 35
        ),
        Game(
            title = "Palworld",
            platform = "PC",
            status = GameStatus.TO_DO,
            personalRating = 0,
            description = "Pokemon-like survival game.",
            releaseDate = "2024-01-18",
            hoursPlayed = 0
        ),
        Game(
            title = "Helldivers 2",
            platform = "PS5",
            status = GameStatus.IN_PROGRESS,
            personalRating = 8,
            description = "Co-op top-down shooter. Super fun with friends.",
            releaseDate = "2024-02-08",
            hoursPlayed = 20
        )
    )
}

