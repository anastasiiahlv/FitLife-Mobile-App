package com.example.fitlife.data.local.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.fitlife.data.local.entity.FitnessCenterEntity
import com.example.fitlife.data.local.entity.TypeEntity
import com.example.fitlife.data.local.entity.FitnessCenterTypeCrossRef

data class FitnessCenterWithTypes(
    @Embedded val center: FitnessCenterEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = FitnessCenterTypeCrossRef::class,
            parentColumn = "fitness_center_id",
            entityColumn = "type_id"
        )
    )
    val types: List<TypeEntity>
)
