package com.example.listmaker.daoObjects

import androidx.room.*
import com.example.listmaker.models.City
import com.example.listmaker.models.Landmark
import kotlinx.coroutines.flow.Flow

@Dao
interface LandmarkDao {

    @Query("SELECT * FROM Landmark ORDER BY landmarkName ASC")
    fun getItems(): Flow<List<Landmark>>

    @Query("SELECT * FROM Landmark WHERE landmarkId = :landmarkId")
    fun getItem(landmarkId: Int): Flow<Landmark>

    @Query("SELECT * FROM Landmark WHERE cityId = :cityId")
    fun getLandmarksByCityId(cityId: Int): Flow<List<Landmark>>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Landmark)

    @Update
    suspend fun update(item: Landmark)

    @Delete
    suspend fun delete(item: Landmark)
}