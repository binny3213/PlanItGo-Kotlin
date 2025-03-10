package com.example.planitgo_finalproject.data.remote_db

import com.example.planitgo_finalproject.data.models.NearbyPlacesResponse
import com.example.planitgo_finalproject.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NearbyApiService {
    @GET("place/nearbysearch/json")
    suspend fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int = 1000,
        @Query("type") type: String = "tourist_attraction",
        @Query("language") language: String,
        @Query("key") apiKey: String = Constants.PLACES_API_KEY
    ): Response<NearbyPlacesResponse>
}