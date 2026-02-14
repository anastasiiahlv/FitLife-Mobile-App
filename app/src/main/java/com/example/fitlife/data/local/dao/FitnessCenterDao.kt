package com.example.fitlife.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.fitlife.data.local.entity.FitnessCenterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessCenterDao {

    // --------- Inserts / Seed ----------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(centers: List<FitnessCenterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(center: FitnessCenterEntity)

    // --------- Basic reads ----------
    @Query("SELECT * FROM fitness_centers ORDER BY name ASC")
    fun observeAll(): Flow<List<FitnessCenterEntity>>

    @Query("SELECT * FROM fitness_centers ORDER BY name ASC")
    suspend fun getAll(): List<FitnessCenterEntity>

    @Query("SELECT * FROM fitness_centers WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): FitnessCenterEntity?

    // --------- Search ----------
    @Query("""
        SELECT * FROM fitness_centers
        WHERE name LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun observeByName(query: String): Flow<List<FitnessCenterEntity>>

    @Query("""
        SELECT * FROM fitness_centers
        WHERE name LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    suspend fun searchByName(query: String): List<FitnessCenterEntity>

    // --------- Filters ----------
    @Query("""
        SELECT * FROM fitness_centers
        WHERE rating >= :minRating
        ORDER BY rating DESC
    """)
    suspend fun filterByMinRating(minRating: Double): List<FitnessCenterEntity>
}
