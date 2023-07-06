package com.example.listmaker

import android.app.Application
import androidx.room.RoomDatabase
import com.example.listmaker.appDatabase.RoomData

class TravelApplication : Application() {
    // Using by lazy so the database is only created when needed
    // rather than when the application starts
    val database: RoomDatabase by lazy {RoomData.getDatabase(this)}
}