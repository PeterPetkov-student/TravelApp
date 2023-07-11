package com.example.listmaker.daoObjects

import androidx.room.*
import com.example.listmaker.models.City
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Query("SELECT * from City ORDER BY cityName ASC")
    fun getItems(): Flow<List<City>>

    @Query("SELECT * from City WHERE cityId = :cityId")
    fun getItem(cityId: Int): Flow<City>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: City)

    @Update
    suspend fun update(item: City)

    @Delete
    suspend fun delete(item: City)
}