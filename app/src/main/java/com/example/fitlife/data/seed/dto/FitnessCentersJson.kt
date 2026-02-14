package com.example.fitlife.data.seed.dto

data class FitnessCentersJson(
    val fitnessCenters: List<FitnessCenterJson>
)

data class FitnessCenterJson(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double,
    val schedule: String? = null,
    val phone: String? = null,
    val website: String? = null,
    val description: String? = null,
    val types: List<String> = emptyList(),
    val services: List<ServiceJson> = emptyList()
)

data class ServiceJson(
    val name: String,
    val price: Double
)
