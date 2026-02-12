package fr.sdv.gamebacklog.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface FreeToGameApiService {

    @GET("games")
    suspend fun getAllGames(): List<FreeGameResponse>

    @GET("games")
    suspend fun getGamesPaginated(
        @Query("platform") platform: String? = null,
        @Query("category") category: String? = null,
        @Query("sort-by") sortBy: String? = null
    ): List<FreeGameResponse>

    @GET("games")
    suspend fun getGamesByPlatform(
        @Query("platform") platform: String
    ): List<FreeGameResponse>

    @GET("games")
    suspend fun getGamesByCategory(
        @Query("category") category: String
    ): List<FreeGameResponse>

    @GET("games")
    suspend fun getGamesSorted(
        @Query("sort-by") sortBy: String
    ): List<FreeGameResponse>

    @GET("games")
    suspend fun getGamesFiltered(
        @Query("platform") platform: String? = null,
        @Query("category") category: String? = null,
        @Query("sort-by") sortBy: String? = null
    ): List<FreeGameResponse>

    @GET("game")
    suspend fun getGameById(
        @Query("id") gameId: Int
    ): FreeGameDetailResponse
}
