package com.example.planitgo_finalproject.data.remote_db

import retrofit2.http.Query
import com.example.planitgo_finalproject.data.models.GeoapifyResponse
import com.example.planitgo_finalproject.utils.Constants
import retrofit2.Response
import retrofit2.http.GET

interface GeoApiService {
    @GET("v1/geocode/autocomplete")
    suspend fun getDestinationAutoComplete(
        @Query("text") text: String,
        @Query("apiKey") apiKey: String = Constants.GEOAPIFY_API_KEY
    ): Response<GeoapifyResponse>
}
