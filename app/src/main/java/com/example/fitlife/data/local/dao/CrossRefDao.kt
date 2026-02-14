package com.example.fitlife.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.fitlife.data.local.entity.FitnessCenterServiceCrossRef
import com.example.fitlife.data.local.entity.FitnessCenterTypeCrossRef

@Dao
interface CrossRefDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCenterTypes(items: List<FitnessCenterTypeCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCenterServices(items: List<FitnessCenterServiceCrossRef>)
}
