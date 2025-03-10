package com.example.planitgo_finalproject.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_attraction_table")
data class ApiAttraction(
    @PrimaryKey
    @ColumnInfo(name = "api_place_id")
    val placeId: String,
    @ColumnInfo(name = "api_attraction_name")
    val name: String?  = null,
    @ColumnInfo(name = "api_attraction_rate")
    val rating: String? = null,
    @ColumnInfo(name = "api_attraction_photo")
    val photoReference: String?,
    @ColumnInfo(name = "api_attraction_icon")
    val icon: String? = null,
    @ColumnInfo(name = "api_attraction_latitude")
    var latitude: Double? = null,
    @ColumnInfo(name = "api_attraction_longitude")
    var longitude: Double? = null,
    @ColumnInfo(name = "api_attraction_address")
    val address: String? = null,
    @ColumnInfo(name = "api_description")
    var description: String? = null,
    @ColumnInfo(name = "api_opening_hours")
    var openingHours: String? = null,
    @ColumnInfo(name = "api_photo_url")
    var photoURL: String? = null,
    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean = false
)
