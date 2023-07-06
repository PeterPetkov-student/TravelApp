package com.example.listmaker.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class City(
    @PrimaryKey(autoGenerate = true)
    val cityId: Int = 0,
    val cityName: String,
    val cityDescription: String,
)