package com.example.fitlife.data.file.dto

import com.example.fitlife.data.local.entity.VisitEntity

fun VisitEntity.toVisitJson(): VisitJson {
    return VisitJson(
        id = id,
        centerId = center_id,
        visitDate = visit_date,
        comment = comment
    )
}

fun VisitJson.toVisitEntity(): VisitEntity {
    return VisitEntity(
        id = id,
        center_id = centerId,
        visit_date = visitDate,
        comment = comment
    )
}