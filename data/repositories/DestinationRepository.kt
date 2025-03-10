package com.example.planitgo_finalproject.data.repositories

import android.util.Log
import com.example.planitgo_finalproject.data.local_db.DestinationDao
import com.example.planitgo_finalproject.data.models.CountryDetails
import com.example.planitgo_finalproject.data.models.Destination
import com.example.planitgo_finalproject.data.models.FlagResponse
import com.example.planitgo_finalproject.data.remote_db.DestinationRemoteDataSource
import com.example.planitgo_finalproject.utils.Success
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DestinationRepository @Inject constructor(
    private val remoteData : DestinationRemoteDataSource,
    private val localData : DestinationDao,
) {
    fun getDestinationDetailsApi(query: String) = fetchAutoComplete {remoteData.getDestinationDetails(query)}

    suspend fun fetchFlagAndSaveDestination(countryDetails: CountryDetails) {
        try {
            val response = remoteData.getCountryFlag(countryDetails.country)
            if (response.status is Success) {
                val flagUrl = (response.status.data as ArrayList<FlagResponse>)[0].flags.png
                createDestinationObjectAndSave(countryDetails, flagUrl)
            } else {
                val imageUrl = "android.resource://com.example.planitgo_finalproject/drawable/airplain1"
                createDestinationObjectAndSave(countryDetails, imageUrl)
                Log.e("Repository", "Error fetching flag")
            }

        } catch (e: Exception) {
            Log.e("Repository", "Exception: ${e.localizedMessage}")
        }
    }
    fun getAllDestinations() = localData.getAllDestinations()
    suspend fun deleteDestination(destination: Destination) = localData.deleteDestination(destination)
    suspend fun deleteAttractionFromDestination(lon: Double, lat:Double) {
        localData.deleteAttractionAPIFromCountry(lon,lat)
        localData.deleteAttractionCustomFromCountry(lon,lat)
    }
    private suspend fun createDestinationObjectAndSave(countryDetails: CountryDetails, flag: String){
        val destination = Destination(
            country = countryDetails.country,
            city = countryDetails.city,
            lon = countryDetails.lon,
            lat = countryDetails.lat,
            flagImg = flag
        )
        localData.addDestination(destination)
    }
}