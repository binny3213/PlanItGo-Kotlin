package com.example.planitgo_finalproject.data.remote_db

import com.example.planitgo_finalproject.di.DestinationModule
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DestinationRemoteDataSource @Inject constructor(
    @DestinationModule.CountryApiRetrofit private val countryApiService: CountryApiService,
    @DestinationModule.GeoApiRetrofit private val geoApiService: GeoApiService
) : BaseDataSource() {
    suspend fun getDestinationDetails(countryName: String) = getResult { geoApiService.getDestinationAutoComplete(countryName) }
    suspend fun getCountryFlag(countryName: String) = getResult { countryApiService.getCountryFlag(countryName) }
}