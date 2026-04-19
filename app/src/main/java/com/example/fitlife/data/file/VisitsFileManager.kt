package com.example.fitlife.data.file

import android.content.Context
import com.example.fitlife.data.local.entity.VisitEntity
import com.example.fitlife.data.file.dto.VisitJson
import com.example.fitlife.data.file.dto.VisitsExportJson
import com.example.fitlife.data.file.dto.toVisitEntity
import com.example.fitlife.data.file.dto.toVisitJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class VisitsFileManager(
    private val context: Context
) {

    private val fileName = "visits_export.json"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val adapter = moshi.adapter(VisitsExportJson::class.java)

    private fun getFile(): File {
        return File(context.filesDir, fileName)
    }

    suspend fun exportVisits(visits: List<VisitEntity>) = withContext(Dispatchers.IO) {
        val file = getFile()

        val exportJson = VisitsExportJson(
            visits = visits.map { it.toVisitJson() }
        )

        val jsonText = adapter.toJson(exportJson)
        file.writeText(jsonText)
    }

    suspend fun readVisits(): List<VisitEntity> = withContext(Dispatchers.IO) {
        val file = getFile()

        if (!file.exists()) {
            return@withContext emptyList()
        }

        val jsonText = file.readText()
        val parsed = adapter.fromJson(jsonText) ?: return@withContext emptyList()

        parsed.visits.map(VisitJson::toVisitEntity)
    }

    suspend fun deleteVisitsFile(): Boolean = withContext(Dispatchers.IO) {
        val file = getFile()
        if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    suspend fun visitsFileExists(): Boolean = withContext(Dispatchers.IO) {
        getFile().exists()
    }

    suspend fun getVisitsFilePath(): String = withContext(Dispatchers.IO) {
        getFile().absolutePath
    }
}