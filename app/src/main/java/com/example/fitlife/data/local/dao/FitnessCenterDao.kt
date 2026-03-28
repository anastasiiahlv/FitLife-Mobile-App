package com.example.fitlife.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitlife.data.local.entity.FitnessCenterEntity
import com.example.fitlife.data.local.relation.FitnessCenterServiceWithPrice
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessCenterDao {

    // ---------- Seed / basic ----------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FitnessCenterEntity>)

    @Query("SELECT * FROM fitness_centers")
    suspend fun getAll(): List<FitnessCenterEntity>

    // ---------- List filters (Stage 2) ----------
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

    // ---------- Details (Stage 3) ----------
    @Query(
        """
        SELECT * 
        FROM fitness_centers
        WHERE id = :centerId
        LIMIT 1
        """
    )
    fun observeCenterById(centerId: String): Flow<FitnessCenterEntity?>

    @Query(
        """
        SELECT t.name
        FROM types t
        INNER JOIN fitness_centers_types fct ON fct.type_id = t.id
        WHERE fct.fitness_center_id = :centerId
        ORDER BY t.name COLLATE NOCASE
        """
    )
    fun observeTypeNamesForCenter(centerId: String): Flow<List<String>>

    @Query(
        """
        SELECT 
            s.id AS serviceId,
            s.name AS serviceName,
            fcs.price AS price
        FROM fitness_centers_services fcs
        INNER JOIN services s ON s.id = fcs.service_id
        WHERE fcs.fitness_center_id = :centerId
        ORDER BY fcs.price ASC, s.name COLLATE NOCASE
        """
    )
    fun observeServicesForCenter(centerId: String): Flow<List<FitnessCenterServiceWithPrice>>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM favorites f WHERE f.center_id = :centerId
        )
        """
    )
    fun observeIsFavorite(centerId: String): Flow<Boolean>
}