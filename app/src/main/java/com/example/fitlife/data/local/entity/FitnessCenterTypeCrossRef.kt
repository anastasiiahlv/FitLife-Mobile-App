package com.example.fitlife.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "fitness_centers_types",
    primaryKeys = ["fitness_center_id", "type_id"],
    foreignKeys = [
        ForeignKey(
            entity = FitnessCenterEntity::class,
            parentColumns = ["id"],
            childColumns = ["fitness_center_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["type_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["fitness_center_id"]),
        Index(value = ["type_id"])
    ]
)
data class FitnessCenterTypeCrossRef(
    val fitness_center_id: String,
    val type_id: String
)
