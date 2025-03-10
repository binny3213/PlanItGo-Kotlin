package com.example.planitgo_finalproject.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.planitgo_finalproject.data.models.ApiAttraction
import com.example.planitgo_finalproject.data.models.Destination
import com.example.planitgo_finalproject.data.models.UserCustomAttraction

@Database(entities = arrayOf(Destination::class, ApiAttraction::class, UserCustomAttraction::class), version = 7, exportSchema = false)
abstract class DataBase : RoomDatabase() {
    abstract fun destinationDao() : DestinationDao
    abstract fun attractionDao(): AttractionDao
    companion object{
        @Volatile
        private  var instance:DataBase? = null
        fun getDatabase(context: Context) = instance ?: synchronized(this){
            Room.databaseBuilder(context.applicationContext, DataBase::class.java, "database")
                .fallbackToDestructiveMigration().build().also {
                    instance = it
                }
        }
    }
}