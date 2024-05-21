package com.example.task

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {
    @GET("photos")
    suspend fun getPhotos(
        @Query("client_id") clientId: String,
        @Query("page") page: Int
    ): List<UnsplashResponse>
}

object RetrofitInstance {
    private const val BASE_URL = "https://api.unsplash.com/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: UnsplashApi by lazy {
        retrofit.create(UnsplashApi::class.java)
    }
}
