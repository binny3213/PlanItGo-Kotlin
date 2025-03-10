package com.example.planitgo_finalproject.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.planitgo_finalproject.data.models.Destination

@Dao
interface DestinationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDestination(destination: Destination)
    @Delete
    suspend fun deleteDestination(destination: Destination)
    @Query("SELECT * FROM destination")
    fun getAllDestinations() : LiveData<List<Destination>>
    @Query("DELETE FROM api_attraction_table  WHERE api_attraction_longitude = :lon AND api_attraction_latitude = :lat")
    suspend fun deleteAttractionAPIFromCountry(lon: Double, lat: Double)
    @Query("DELETE FROM user_attraction_table  WHERE attraction_longitude = :lon AND attraction_latitude = :lat")
    suspend fun deleteAttractionCustomFromCountry(lon: Double, lat: Double)
}