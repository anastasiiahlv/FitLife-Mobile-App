package com.example.fitlife.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "types",
    indices = [Index(value = ["name"], unique = true)]
)
data class TypeEntity(
    @PrimaryKey
    val id: String,
    val name: String
)
