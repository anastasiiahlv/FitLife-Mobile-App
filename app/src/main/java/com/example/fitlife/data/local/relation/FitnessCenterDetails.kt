package com.example.fitlife.data.local.relation

import androidx.room.Embedded
import com.example.fitlife.data.local.entity.FitnessCenterEntity

data class FitnessCenterDetails(
    @Embedded val center: FitnessCenterEntity,
    val types: List<String>,
    val services: List<FitnessCenterServiceWithPrice>,
    val isFavorite: Boolean
)
