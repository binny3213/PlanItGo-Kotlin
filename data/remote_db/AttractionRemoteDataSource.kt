package com.example.planitgo_finalproject.data.remote_db
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttractionRemoteDataSource @Inject constructor(
    private val nearbyService: NearbyApiService,
    private val placeDetailsService: PlaceDetailsService
) : BaseDataSource() {
    suspend fun getAllAttractions(location: String, language: String) = getResult { nearbyService.getNearbyPlaces(location = location, language = language) }
    suspend fun getPlaceDetailsByPlaceId(placeId: String,language: String) = getResult { placeDetailsService.getPlaceDetails(placeId = placeId, language = language) }
}