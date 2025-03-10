package com.example.planitgo_finalproject.data.remote_db

import com.example.planitgo_finalproject.utils.Resource
import retrofit2.Response

abstract class BaseDataSource {
    protected suspend fun <T> getResult(call : suspend () -> Response<T>) : Resource<T>{
        try {
            val result = call()
            if(result.isSuccessful){
                val body = result.body()
                if(body != null) return Resource.success(body)
            }
            return Resource.error("Network failed for a following reason:" + "${result.message()} ${result.code()}")
        }catch (e : Exception){
            return Resource.error("Network failed for a following reason:" + (e.localizedMessage ?: e.toString()))
        }
    }
}