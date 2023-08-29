package com.example.listmaker.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Landmark(
    @PrimaryKey(autoGenerate = true)
    val landmarkId: Int = 0,
    val landmarkName: String,
    val landmarkDescription: String,
    val cityId: Int
)