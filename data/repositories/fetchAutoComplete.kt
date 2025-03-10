package com.example.planitgo_finalproject.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.planitgo_finalproject.data.models.GeoapifyResponse
import com.example.planitgo_finalproject.utils.Failure
import com.example.planitgo_finalproject.utils.Resource
import com.example.planitgo_finalproject.utils.Success
import kotlinx.coroutines.Dispatchers

fun fetchAutoComplete(remoteFetch: suspend () ->  Resource<GeoapifyResponse>) : LiveData<Resource<GeoapifyResponse>> =
    liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val response = remoteFetch()
        if(response.status is Success){
            emit(Resource.success(response.status.data!!))
        }
        else if (response.status is Failure) {
            emit(Resource.error(response.status.message))
            emit(Resource.loading())
        }
    }