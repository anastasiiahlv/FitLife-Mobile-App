package com.example.fitlife.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitlife.data.local.entity.FavoriteEntity
import com.example.fitlife.data.local.entity.FitnessCenterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE center_id = :centerId")
    suspend fun removeFromFavorites(centerId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE center_id = :centerId)")
    suspend fun isFavorite(centerId: String): Boolean

    // Список ID обраного
    @Query("SELECT center_id FROM favorites ORDER BY addedAt DESC")
    fun observeFavoriteIds(): Flow<List<String>>

    // Список обраних центрів (JOIN)
    @Query("""
        SELECT fc.* FROM fitness_centers fc
        JOIN favorites f ON f.center_id = fc.id
        ORDER BY f.addedAt DESC
    """)
    fun observeFavoriteCenters(): Flow<List<FitnessCenterEntity>>

    @Query("""
        SELECT fc.* FROM fitness_centers fc
        JOIN favorites f ON f.center_id = fc.id
        ORDER BY f.addedAt DESC
    """)
    suspend fun getFavoriteCenters(): List<FitnessCenterEntity>
}
