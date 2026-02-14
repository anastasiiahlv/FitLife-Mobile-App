package com.example.fitlife.data.seed

import android.content.Context
import com.example.fitlife.data.local.AppDatabase
import com.example.fitlife.data.local.entity.FitnessCenterEntity
import com.example.fitlife.data.local.entity.FitnessCenterServiceCrossRef
import com.example.fitlife.data.local.entity.FitnessCenterTypeCrossRef
import com.example.fitlife.data.local.entity.ServiceEntity
import com.example.fitlife.data.local.entity.TypeEntity
import com.example.fitlife.data.seed.dto.FitnessCentersJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

object DatabaseSeeder {

    suspend fun seedIfNeeded(context: Context, db: AppDatabase) {
        // Якщо в БД вже є центри — нічого не робимо
        val alreadySeeded = db.fitnessCenterDao().getAll().isNotEmpty()
        if (alreadySeeded) return

        withContext(Dispatchers.IO) {
            val jsonText = context.assets.open("fitness_centers.json")
                .bufferedReader()
                .use { it.readText() }

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val adapter = moshi.adapter(FitnessCentersJson::class.java)
            val parsed = adapter.fromJson(jsonText)
                ?: error("Cannot parse fitness_centers.json")

            // 1) Centers
            val centerEntities = parsed.fitnessCenters.map { c ->
                FitnessCenterEntity(
                    id = c.id,
                    name = c.name,
                    address = c.address,
                    latitude = c.latitude,
                    longitude = c.longitude,
                    rating = c.rating,
                    schedule = c.schedule,
                    phone = c.phone,
                    website = c.website,
                    description = c.description
                )
            }
            db.fitnessCenterDao().insertAll(centerEntities)

            // 2) Types (унікальні)
            val uniqueTypes = parsed.fitnessCenters
                .flatMap { it.types }
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()

            val typeEntities = uniqueTypes.map { typeName ->
                TypeEntity(
                    id = "type_" + typeName.lowercase().replace(" ", "_"),
                    name = typeName
                )
            }
            db.typesDao().insertAll(typeEntities)

            val typeIdByName = typeEntities.associate { it.name to it.id }

            val centerTypeRefs = parsed.fitnessCenters.flatMap { c ->
                c.types.mapNotNull { typeName ->
                    val typeId = typeIdByName[typeName]
                    typeId?.let {
                        FitnessCenterTypeCrossRef(
                            fitness_center_id = c.id,
                            type_id = it
                        )
                    }
                }
            }
            db.crossRefDao().insertCenterTypes(centerTypeRefs)

            // 3) Services (унікальні по назві)
            val uniqueServices = parsed.fitnessCenters
                .flatMap { it.services }
                .map { it.name.trim() }
                .filter { it.isNotEmpty() }
                .distinct()

            val serviceEntities = uniqueServices.map { serviceName ->
                ServiceEntity(
                    id = "srv_" + serviceName.lowercase().replace(" ", "_"),
                    name = serviceName
                )
            }
            db.servicesDao().insertAll(serviceEntities)

            val serviceIdByName = serviceEntities.associate { it.name to it.id }

            val centerServiceRefs = parsed.fitnessCenters.flatMap { c ->
                c.services.mapNotNull { s ->
                    val serviceId = serviceIdByName[s.name]
                    serviceId?.let {
                        FitnessCenterServiceCrossRef(
                            fitness_center_id = c.id,
                            service_id = it,
                            price = s.price
                        )
                    }
                }
            }
            db.crossRefDao().insertCenterServices(centerServiceRefs)
        }
    }
}
