package com.example.planitgo_finalproject.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.planitgo_finalproject.data.models.ApiAttraction
import com.example.planitgo_finalproject.data.models.NearbyPlacesResponse
import com.example.planitgo_finalproject.utils.Failure
import com.example.planitgo_finalproject.utils.Resource
import com.example.planitgo_finalproject.utils.Success
import kotlinx.coroutines.Dispatchers

fun allAttractionApiFetchAndSave(localDbFetch: () -> LiveData<List<ApiAttraction>>,
                                 remoteDbFetch: suspend () -> Resource<NearbyPlacesResponse>,
                                 localFetchList: suspend () -> List<ApiAttraction>,
                                 transformAndSaving: suspend(NearbyPlacesResponse,List<ApiAttraction>?) -> Unit) : LiveData<Resource<List<ApiAttraction>>> =
        liveData(Dispatchers.IO) {
            emit(Resource.loading())
            val localData = localDbFetch()
            emitSource(localData.map { Resource.success(it) })
            val fetchResource = remoteDbFetch()
            if(fetchResource.status is Success){
                val sourceList = localFetchList()
                transformAndSaving(fetchResource.status.data!!,sourceList)
            } else if (fetchResource.status is Failure) {
                emit(Resource.error(fetchResource.status.message))
                emitSource(localData.map { Resource.success(it) })
            }
        }