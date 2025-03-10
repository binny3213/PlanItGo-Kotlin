package com.example.planitgo_finalproject.di

import NearbyPlacesResponseDeserializer
import com.example.planitgo_finalproject.data.local_db.DataBase
import com.example.planitgo_finalproject.data.models.NearbyPlacesResponse
import com.example.planitgo_finalproject.data.remote_db.NearbyApiService
import com.example.planitgo_finalproject.data.remote_db.PlaceDetailsService

import com.example.planitgo_finalproject.utils.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AttractionModule {
    @Qualifier
    annotation class AttractionApiRetrofit
    @Qualifier
    annotation class AttractionGson
    @Qualifier
    annotation class PlacesDetailsApiRetrofit

    @AttractionApiRetrofit
    @Provides
    @Singleton
    fun provideAttractionRetrofit(@AttractionGson gson: Gson) : Retrofit {
        return Retrofit.Builder().baseUrl(Constants.PLACES_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    }
    @PlacesDetailsApiRetrofit
    @Provides
    @Singleton
    fun providePlaceDetailsRetrofit(gson: Gson) : Retrofit {
        return Retrofit.Builder().baseUrl(Constants.PLACES_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    }
    @Provides
    @Singleton
    @AttractionGson
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(NearbyPlacesResponse::class.java, NearbyPlacesResponseDeserializer())
        return gsonBuilder.create()
    }
    @Provides
    fun provideNearbyApiService(@AttractionApiRetrofit retrofit: Retrofit) : NearbyApiService =
        retrofit.create(NearbyApiService::class.java)
    @Provides
    fun providePlaceDetailsApiService(@PlacesDetailsApiRetrofit retrofit: Retrofit) : PlaceDetailsService =
        retrofit.create(PlaceDetailsService::class.java)
    @Provides
    @Singleton
    fun provideAttractionDao(database: DataBase) = database.attractionDao()
}