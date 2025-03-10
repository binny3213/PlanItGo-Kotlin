package com.example.planitgo_finalproject.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.planitgo_finalproject.data.models.ApiAttraction
import com.example.planitgo_finalproject.data.models.UserCustomAttraction

@Dao
interface AttractionDao {
    //API Attraction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insetAllApiAttraction(attractionsList: List<ApiAttraction>)
    @Query("SELECT* FROM api_attraction_table WHERE api_attraction_longitude = :lon AND api_attraction_latitude = :lat")
    fun getAllApiAttractions(lon: Double, lat: Double): LiveData<List<ApiAttraction>>
    @Query("SELECT* FROM api_attraction_table WHERE api_attraction_longitude = :lon AND api_attraction_latitude = :lat")
    suspend fun getAllApiAttractionsSync(lon: Double, lat: Double): List<ApiAttraction>
    @Query("SELECT* FROM api_attraction_table WHERE api_place_id LIKE :placeId")
    fun getApiAttractionByPlaceId(placeId: String): LiveData<ApiAttraction>
    @Query("SELECT* FROM api_attraction_table WHERE api_attraction_latitude = :latitude AND api_attraction_longitude = :longitude AND is_favorite = true")
    fun getFavoriteApiAttractions(latitude: Double,longitude: Double) : LiveData<List<ApiAttraction>>
    @Update
    suspend fun updateApiAttraction(attraction: ApiAttraction)

    //custom Attraction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCustomAttraction(attraction: UserCustomAttraction)
    @Delete
    suspend fun deleteCustomAttraction(attraction: UserCustomAttraction)
    @Update
    suspend fun updateCustomAttraction(attraction: UserCustomAttraction)
    @Query("SELECT* FROM user_attraction_table WHERE attraction_latitude = :latitude AND attraction_longitude = :longitude")
    fun getAllCustomAttraction(latitude: Double,longitude: Double): LiveData<List<UserCustomAttraction>>
    @Query("SELECT* FROM user_attraction_table WHERE attraction_latitude = :latitude AND attraction_longitude = :longitude AND favorite = true")
    fun getFavoriteCustomAttractions(latitude: Double,longitude: Double) : LiveData<List<UserCustomAttraction>>

}