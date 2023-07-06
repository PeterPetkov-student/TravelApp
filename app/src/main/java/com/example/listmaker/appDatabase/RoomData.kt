package com.example.listmaker.appDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.listmaker.daoObjects.CityDao
import com.example.listmaker.daoObjects.LandmarkDao
import com.example.listmaker.models.City
import com.example.listmaker.models.Landmark

@Database(entities = [City::class, Landmark::class], version = 1, exportSchema = false)
abstract class RoomData : RoomDatabase() {

    abstract fun CityDao(): CityDao
    abstract fun LandmarkDao(): LandmarkDao

    companion object {
        @Volatile
        private var INSTANCE: RoomData? = null

        fun getDatabase(context: Context): RoomData {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomData::class.java,
                    "room_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}