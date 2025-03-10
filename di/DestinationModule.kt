package com.example.planitgo_finalproject.di

import com.example.planitgo_finalproject.data.local_db.DataBase
import com.example.planitgo_finalproject.data.remote_db.CountryApiService
import com.example.planitgo_finalproject.data.remote_db.GeoApiService
import com.example.planitgo_finalproject.utils.Constants
import com.google.gson.Gson
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
object DestinationModule {
    @Qualifier
    annotation class GeoApiRetrofit
    @Qualifier
    annotation class CountryApiRetrofit

    @GeoApiRetrofit
    @Provides
    @Singleton
    fun provideGeoRetrofit(gson: Gson) : Retrofit{
        return Retrofit.Builder().baseUrl(Constants.GEOAPIFY_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    }
    @GeoApiRetrofit
    @Provides
    fun provideGeoApiService(@GeoApiRetrofit retrofit: Retrofit) : GeoApiService =
        retrofit.create(GeoApiService::class.java)

    @CountryApiRetrofit
    @Provides
    @Singleton
    fun provideCountryRetrofit(gson: Gson) : Retrofit{
        return Retrofit.Builder().baseUrl(Constants.COUNTRY_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    }
    @CountryApiRetrofit
    @Provides
    fun provideCountryApiService(@CountryApiRetrofit retrofit: Retrofit) : CountryApiService =
        retrofit.create(CountryApiService::class.java)
    @Provides
    @Singleton
    fun provideDestinationDao(dataBase: DataBase) = dataBase.destinationDao()

}