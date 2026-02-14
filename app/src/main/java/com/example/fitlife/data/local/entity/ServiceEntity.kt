package com.example.fitlife.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "services",
    indices = [Index(value = ["name"], unique = true)]
)
data class ServiceEntity(
    @PrimaryKey
    val id: String,
    val name: String
)
