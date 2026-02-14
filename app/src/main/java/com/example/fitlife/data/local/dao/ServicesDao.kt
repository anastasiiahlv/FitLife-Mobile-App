package com.example.fitlife.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitlife.data.local.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow

// Результат для UI: центр + назва послуги + ціна
data class CenterWithServicePrice(
    val centerId: String,
    val centerName: String,
    val address: String,
    val rating: Double,
    val serviceName: String,
    val price: Double
)

@Dao
interface ServicesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(services: List<ServiceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(service: ServiceEntity)

    @Query("SELECT * FROM services ORDER BY name ASC")
    suspend fun getAll(): List<ServiceEntity>

    @Query("SELECT * FROM services WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchServicesByName(query: String): List<ServiceEntity>

    // Центри, де є послуга з назвою як у запиті і ціною <= maxPrice
    @Query("""
        SELECT 
            fc.id AS centerId,
            fc.name AS centerName,
            fc.address AS address,
            fc.rating AS rating,
            s.name AS serviceName,
            fcs.price AS price
        FROM fitness_centers fc
        JOIN fitness_centers_services fcs ON fcs.fitness_center_id = fc.id
        JOIN services s ON s.id = fcs.service_id
        WHERE s.name LIKE '%' || :serviceQuery || '%'
          AND fcs.price <= :maxPrice
        ORDER BY fcs.price ASC, fc.rating DESC
    """)
    suspend fun findCentersByServiceAndMaxPrice(
        serviceQuery: String,
        maxPrice: Double
    ): List<CenterWithServicePrice>

    // Якщо захочеш реактивно (Flow)
    @Query("""
        SELECT 
            fc.id AS centerId,
            fc.name AS centerName,
            fc.address AS address,
            fc.rating AS rating,
            s.name AS serviceName,
            fcs.price AS price
        FROM fitness_centers fc
        JOIN fitness_centers_services fcs ON fcs.fitness_center_id = fc.id
        JOIN services s ON s.id = fcs.service_id
        WHERE s.name LIKE '%' || :serviceQuery || '%'
          AND fcs.price <= :maxPrice
        ORDER BY fcs.price ASC, fc.rating DESC
    """)
    fun observeCentersByServiceAndMaxPrice(
        serviceQuery: String,
        maxPrice: Double
    ): Flow<List<CenterWithServicePrice>>
}
