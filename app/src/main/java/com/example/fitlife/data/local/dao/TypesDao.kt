package com.example.fitlife.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitlife.data.local.entity.TypeEntity

@Dao
interface TypesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(types: List<TypeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(type: TypeEntity)

    @Query("SELECT * FROM types ORDER BY name ASC")
    suspend fun getAll(): List<TypeEntity>

    @Query("SELECT * FROM types WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): TypeEntity?
}
