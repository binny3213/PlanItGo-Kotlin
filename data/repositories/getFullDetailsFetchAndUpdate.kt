package com.example.planitgo_finalproject.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.planitgo_finalproject.data.models.ApiAttraction
import com.example.planitgo_finalproject.data.models.PlacesDetailsResponse
import com.example.planitgo_finalproject.utils.Failure
import com.example.planitgo_finalproject.utils.Resource
import com.example.planitgo_finalproject.utils.Success
import kotlinx.coroutines.Dispatchers

fun getFullDetailsFetchAndUpdate(localDbFetch: () -> LiveData<ApiAttraction>,
                                       remoteDbFetch: suspend () -> Resource<PlacesDetailsResponse>,
                                       transformDataAndUpdate: suspend(PlacesDetailsResponse) -> Unit) : LiveData<Resource<ApiAttraction>> =
    liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val localData = localDbFetch().map { Resource.success(it) }
        emitSource(localData)
        val fetchResource = remoteDbFetch()
        if(fetchResource.status is Success){
            transformDataAndUpdate(fetchResource.status.data!!)
        } else if (fetchResource.status is Failure) {
            emit(Resource.error(fetchResource.status.message))
            emitSource(localData)
        }
    }