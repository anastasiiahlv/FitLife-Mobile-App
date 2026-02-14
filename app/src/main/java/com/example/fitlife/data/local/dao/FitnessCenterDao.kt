package com.example.fitlife.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitlife.data.local.entity.FitnessCenterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessCenterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FitnessCenterEntity>)

    // Для seed check (ти вже це використовуєш)
    @Query("SELECT * FROM fitness_centers")
    suspend fun getAll(): List<FitnessCenterEntity>

    // ✅ Етап 2: основний запит зі всіма фільтрами (опційні параметри)
    @Query(
        """
        SELECT DISTINCT fc.* 
        FROM fitness_centers fc
        LEFT JOIN fitness_centers_types fct ON fc.id = fct.fitness_center_id
        LEFT JOIN types t ON t.id = fct.type_id
        LEFT JOIN fitness_centers_services fcs ON fc.id = fcs.fitness_center_id
        LEFT JOIN services s ON s.id = fcs.service_id
        WHERE (:nameQuery IS NULL OR fc.name LIKE '%' || :nameQuery || '%')
          AND (:minRating IS NULL OR fc.rating >= :minRating)
          AND (:typeId IS NULL OR t.id = :typeId)
          AND (:serviceQuery IS NULL OR s.name LIKE '%' || :serviceQuery || '%')
          AND (:maxPrice IS NULL OR fcs.price <= :maxPrice)
        ORDER BY fc.name COLLATE NOCASE
        """
    )
    fun observeFiltered(
        nameQuery: String?,
        typeId: String?,
        minRating: Double?,
        serviceQuery: String?,
        maxPrice: Double?
    ): Flow<List<FitnessCenterEntity>>
}
