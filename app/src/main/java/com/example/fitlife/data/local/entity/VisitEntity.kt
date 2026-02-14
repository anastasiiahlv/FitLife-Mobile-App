package com.example.fitlife.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "visits",
    foreignKeys = [
        ForeignKey(
            entity = FitnessCenterEntity::class,
            parentColumns = ["id"],
            childColumns = ["center_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["center_id"]),
        Index(value = ["visit_date"])
    ]
)
data class VisitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val center_id: String,
    val visit_date: Long,

    val comment: String? = null
)
