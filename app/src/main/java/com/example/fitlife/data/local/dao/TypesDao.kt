package com.example.fitlife.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitlife.data.local.entity.TypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TypesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TypeEntity>)

    @Query("SELECT * FROM types ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<TypeEntity>>
}
