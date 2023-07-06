package com.example.listmaker.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity
data class Landmark(
    @PrimaryKey(autoGenerate = false)
    val landmarkName: String,
    val landmarkDescription: String,
    val cityId: Int
)