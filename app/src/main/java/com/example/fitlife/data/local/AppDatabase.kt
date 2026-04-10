package com.example.fitlife.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fitlife.data.local.dao.CrossRefDao
import com.example.fitlife.data.local.dao.FavoritesDao
import com.example.fitlife.data.local.dao.FitnessCenterDao
import com.example.fitlife.data.local.dao.ServicesDao
import com.example.fitlife.data.local.dao.TypesDao
import com.example.fitlife.data.local.dao.VisitsDao
import com.example.fitlife.data.local.entity.FavoriteEntity
import com.example.fitlife.data.local.entity.FitnessCenterEntity
import com.example.fitlife.data.local.entity.FitnessCenterServiceCrossRef
import com.example.fitlife.data.local.entity.FitnessCenterTypeCrossRef
import com.example.fitlife.data.local.entity.ServiceEntity
import com.example.fitlife.data.local.entity.TypeEntity
import com.example.fitlife.data.local.entity.VisitEntity

@Database(
    entities = [
        FitnessCenterEntity::class,
        TypeEntity::class,
        ServiceEntity::class,
        FavoriteEntity::class,
        VisitEntity::class,
        FitnessCenterTypeCrossRef::class,
        FitnessCenterServiceCrossRef::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fitnessCenterDao(): FitnessCenterDao
    abstract fun typesDao(): TypesDao
    abstract fun servicesDao(): ServicesDao
    abstract fun crossRefDao(): CrossRefDao

    abstract fun favoritesDao(): FavoritesDao
    abstract fun visitsDao(): VisitsDao
}
