package fr.sdv.gamebacklog.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Modèle pour les jeux venant de l'API FreeToGame
 */
data class FreeGameResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("short_description")
    val shortDescription: String,
    @SerializedName("game_url")
    val gameUrl: String,
    @SerializedName("genre")
    val genre: String,
    @SerializedName("platform")
    val platform: String,
    @SerializedName("publisher")
    val publisher: String,
    @SerializedName("developer")
    val developer: String,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("freetogame_profile_url")
    val freetogameProfileUrl: String
)

data class FreeGameDetailResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("game_url")
    val gameUrl: String,
    @SerializedName("genre")
    val genre: String,
    @SerializedName("platform")
    val platform: String,
    @SerializedName("publisher")
    val publisher: String,
    @SerializedName("developer")
    val developer: String,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("freetogame_profile_url")
    val freetogameProfileUrl: String,
    @SerializedName("minimum_system_requirements")
    val systemRequirements: SystemRequirements?
)

data class SystemRequirements(
    @SerializedName("os")
    val os: String?,
    @SerializedName("processor")
    val processor: String?,
    @SerializedName("memory")
    val memory: String?,
    @SerializedName("graphics")
    val graphics: String?,
    @SerializedName("storage")
    val storage: String?
)

