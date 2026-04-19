package com.example.fitlife.data.file.dto

data class VisitsExportJson(
    val visits: List<VisitJson>
)

data class VisitJson(
    val id: Int,
    val centerId: String,
    val visitDate: Long,
    val comment: String? = null
)