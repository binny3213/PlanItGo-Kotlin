package com.example.planitgo_finalproject.data.remote_db

import com.example.planitgo_finalproject.data.models.FlagResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CountryApiService {
    @GET("name/{name}")
    suspend fun getCountryFlag(@Path("name") name: String) : Response<ArrayList<FlagResponse>>
}