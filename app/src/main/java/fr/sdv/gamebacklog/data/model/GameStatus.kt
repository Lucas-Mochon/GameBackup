package fr.sdv.gamebacklog.data.model

enum class GameStatus {
    TO_DO,
    IN_PROGRESS,
    DONE;

    fun getLabel(): String = when (this) {
        TO_DO -> "À faire"
        IN_PROGRESS -> "En cours"
        DONE -> "Terminé"
    }
}

