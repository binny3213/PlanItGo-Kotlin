package com.example.planitgo_finalproject.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_attraction_table")
data class UserCustomAttraction(
    @ColumnInfo(name = "attraction_name")
    val name: String,
    @ColumnInfo(name = "location")
    val location: String,
    @ColumnInfo(name = "attraction_latitude")
    val latitude: Double? = null,
    @ColumnInfo(name = "attraction_longitude")
    val longitude: Double? = null,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "opening_time")
    val openingTime: String,
    @ColumnInfo(name = "closing_time")
    val endTime: String,
    @ColumnInfo(name = "price")
    val price: Double,
    @ColumnInfo(name = "favorite")
    var isFavorite: Boolean = false,
    @ColumnInfo(name = "image")
    val image: String,
)
{
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}

