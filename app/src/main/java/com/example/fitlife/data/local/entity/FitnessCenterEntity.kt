package com.example.fitlife.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fitness_centers",
    indices = [
        Index(value = ["name"]),
        Index(value = ["rating"])
    ]
)
data class FitnessCenterEntity(
    @PrimaryKey
    val id: String,

    val name: String,
    val address: String,

    val latitude: Double,
    val longitude: Double,

    val rating: Double,

    val schedule: String? = null,
    val phone: String? = null,
    val website: String? = null,
    val description: String? = null
)
