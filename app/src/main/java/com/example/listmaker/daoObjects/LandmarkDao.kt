package com.example.listmaker.daoObjects

import androidx.room.*
import com.example.listmaker.models.Landmark
import kotlinx.coroutines.flow.Flow

@Dao
interface LandmarkDao {

    @Query("SELECT * from Landmark ORDER BY name ASC")
    fun getItems(): Flow<List<Landmark>>

    @Query("SELECT * from Landmark WHERE landmarkName = :landmarkName")
    fun getItem(landmarkName: String): Flow<Landmark>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Landmark)

    @Update
    suspend fun update(item: Landmark)

    @Delete
    suspend fun delete(item: Landmark)
}