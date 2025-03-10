package com.example.planitgo_finalproject.data.repositories

import android.util.Log
import com.example.planitgo_finalproject.data.local_db.AttractionDao
import com.example.planitgo_finalproject.data.models.ApiAttraction
import com.example.planitgo_finalproject.data.models.NearbyPlacesResponse
import com.example.planitgo_finalproject.data.models.PlacesDetailsResponse
import com.example.planitgo_finalproject.data.models.UserCustomAttraction
import com.example.planitgo_finalproject.data.remote_db.AttractionRemoteDataSource
import com.example.planitgo_finalproject.utils.Constants
import com.example.planitgo_finalproject.utils.getDeviceLanguage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttractionRepository @Inject constructor(
    private val localData: AttractionDao,
    private val remoteData: AttractionRemoteDataSource
){
    //api attraction
    fun getAllApiAttractions(lon: Double, lat: Double) = allAttractionApiFetchAndSave(
    { localData.getAllApiAttractions(lon, lat) },
    { remoteData.getAllAttractions("$lat,$lon", getDeviceLanguage()) },
    { localData.getAllApiAttractionsSync(lon, lat) },
    { apiResponse, localSource -> updateAndSavingNearbyAttractions(apiResponse, lon, lat ,localSource)}
    )

    fun getFavoriteApiAttractions(lon: Double, lat: Double) = localData.getFavoriteApiAttractions(lat,lon)
    suspend fun updateApiAttraction(attraction: ApiAttraction) = localData.updateApiAttraction(attraction)
    fun getAttractionDetails(placeId: String, attraction: ApiAttraction) = getFullDetailsFetchAndUpdate(
        { localData.getApiAttractionByPlaceId(placeId) },
        { remoteData.getPlaceDetailsByPlaceId(placeId, getDeviceLanguage()) },
        { updateDetailsAttractionApi(attraction, it) }
    )
    private suspend fun updateDetailsAttractionApi(attraction: ApiAttraction, placeDetails: PlacesDetailsResponse){
        attraction.description = placeDetails.result.editorial_summary?.overview
        attraction.openingHours = placeDetails.result.opening_hours?.weekday_text?.joinToString("\n")
        try {
            attraction.photoURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=${attraction.photoReference}&key=${Constants.PLACES_API_KEY}"
        } catch (e: Exception) {
            attraction.photoURL = null
            Log.e("AttractionUpdate", "Error setting photo URL: ${e.message}")
        }
        localData.updateApiAttraction(attraction)
    }
    private suspend fun updateAndSavingNearbyAttractions(response: NearbyPlacesResponse,lon: Double, lat: Double, localSource:List<ApiAttraction>?){
        if(response.results.isNotEmpty()){
            response.results.forEach { apiAttraction ->
                val localAttraction = localSource?.find { it.placeId == apiAttraction.placeId }
                localAttraction?.let {
                    apiAttraction.isFavorite = it.isFavorite
                    apiAttraction.description = it.description
                    apiAttraction.openingHours = it.openingHours
                    apiAttraction.photoURL = it.photoURL
                }
                apiAttraction.longitude = lon
                apiAttraction.latitude = lat
            }
            localData.insetAllApiAttraction(response.results)
        }

    }

    //custom attraction

    fun getCustomAttractions(lon: Double, lat: Double) = localData.getAllCustomAttraction(lat,lon)
    fun getFavoriteCustomAttractions(lon: Double, lat: Double) = localData.getFavoriteCustomAttractions(lat,lon)
    suspend fun addCustomAttraction(attraction: UserCustomAttraction) = localData.addCustomAttraction(attraction)
    suspend fun deleteCustomAttraction(attraction: UserCustomAttraction) = localData.deleteCustomAttraction(attraction)
    suspend fun updateCustomAttraction(attraction: UserCustomAttraction) = localData.updateCustomAttraction(attraction)


}