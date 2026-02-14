package com.example.fitlife.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "fitness_centers_services",
    primaryKeys = ["fitness_center_id", "service_id"],
    foreignKeys = [
        ForeignKey(
            entity = FitnessCenterEntity::class,
            parentColumns = ["id"],
            childColumns = ["fitness_center_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ServiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["service_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["fitness_center_id"]),
        Index(value = ["service_id"]),
        Index(value = ["price"])
    ]
)
data class FitnessCenterServiceCrossRef(
    val fitness_center_id: String,
    val service_id: String,
    val price: Double
)
