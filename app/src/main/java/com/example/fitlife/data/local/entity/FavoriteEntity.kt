package com.example.fitlife.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "favorites",
    primaryKeys = ["center_id"],
    foreignKeys = [
        ForeignKey(
            entity = FitnessCenterEntity::class,
            parentColumns = ["id"],
            childColumns = ["center_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["center_id"])]
)
data class FavoriteEntity(
    val center_id: String,
    val addedAt: Long // timestamp (ms). Simple for Room
)
