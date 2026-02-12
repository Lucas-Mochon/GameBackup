package fr.sdv.gamebacklog.data.repository

import fr.sdv.gamebacklog.data.remote.FreeToGameClient
import fr.sdv.gamebacklog.data.remote.FreeGameDetailResponse
import fr.sdv.gamebacklog.data.remote.FreeGameResponse

class FreeGamesRepository {

    private val apiService = FreeToGameClient.apiService

    suspend fun getAllGames(): List<FreeGameResponse> {
        return try {
            apiService.getAllGames()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getGamesByPlatform(platform: String): List<FreeGameResponse> {
        return try {
            apiService.getGamesByPlatform(platform)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getGamesByCategory(category: String): List<FreeGameResponse> {
        return try {
            apiService.getGamesByCategory(category)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getGamesSorted(sortBy: String): List<FreeGameResponse> {
        return try {
            apiService.getGamesSorted(sortBy)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getGamesFiltered(
        platform: String? = null,
        category: String? = null,
        sortBy: String? = null
    ): List<FreeGameResponse> {
        return try {
            apiService.getGamesFiltered(platform, category, sortBy)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getGameById(gameId: Int): FreeGameDetailResponse? {
        return try {
            apiService.getGameById(gameId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

