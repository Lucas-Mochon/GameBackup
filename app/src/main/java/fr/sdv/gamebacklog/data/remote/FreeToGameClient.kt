package fr.sdv.gamebacklog.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Client Retrofit pour l'API FreeToGame
 */
object FreeToGameClient {

    private const val BASE_URL = "https://www.freetogame.com/api/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: FreeToGameApiService = retrofit.create(FreeToGameApiService::class.java)
}

