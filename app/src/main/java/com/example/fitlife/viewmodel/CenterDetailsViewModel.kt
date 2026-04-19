package com.example.fitlife.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlife.data.file.VisitsFileManager
import com.example.fitlife.data.local.DatabaseProvider
import com.example.fitlife.data.local.entity.FavoriteEntity
import com.example.fitlife.data.local.entity.VisitEntity
import com.example.fitlife.data.local.relation.FitnessCenterDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CenterDetailsViewModel(app: Application) : AndroidViewModel(app) {

    private val appContext = app.applicationContext
    private val db = DatabaseProvider.get(app)
    private val centersDao = db.fitnessCenterDao()
    private val favoritesDao = db.favoritesDao()
    private val visitsDao = db.visitsDao()
    private val visitsFileManager = VisitsFileManager(appContext)

    private val _fileActionMessage = MutableStateFlow<String?>(null)
    val fileActionMessage: StateFlow<String?> = _fileActionMessage.asStateFlow()

    fun details(centerId: String): StateFlow<FitnessCenterDetails?> {
        val centerFlow = centersDao.observeCenterById(centerId)
        val typesFlow = centersDao.observeTypeNamesForCenter(centerId)
        val servicesFlow = centersDao.observeServicesForCenter(centerId)
        val favFlow = centersDao.observeIsFavorite(centerId)

        return combine(
            centerFlow,
            typesFlow,
            servicesFlow,
            favFlow
        ) { center, types, services, isFav ->
            center?.let {
                FitnessCenterDetails(
                    center = it,
                    types = types,
                    services = services,
                    isFavorite = isFav
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    }

    fun visits(centerId: String): Flow<List<VisitEntity>> {
        return visitsDao.observeByCenter(centerId)
    }

    fun toggleFavorite(centerId: String, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            if (isCurrentlyFavorite) {
                favoritesDao.removeFromFavorites(centerId)
            } else {
                favoritesDao.addToFavorites(
                    FavoriteEntity(
                        center_id = centerId,
                        addedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun addVisit(centerId: String, visitDate: Long, comment: String?) {
        viewModelScope.launch {
            visitsDao.insert(
                VisitEntity(
                    id = 0,
                    center_id = centerId,
                    visit_date = visitDate,
                    comment = comment?.trim().takeIf { !it.isNullOrEmpty() }
                )
            )
        }
    }

    fun deleteVisit(visitId: Int) {
        viewModelScope.launch {
            visitsDao.deleteById(visitId)
        }
    }

    fun updateVisitComment(visitId: Int, comment: String?) {
        viewModelScope.launch {
            visitsDao.updateComment(
                visitId = visitId,
                comment = comment?.trim().takeIf { !it.isNullOrEmpty() }
            )
        }
    }

    fun exportVisitsToFile() {
        viewModelScope.launch {
            try {
                val visits = visitsDao.getAll()
                visitsFileManager.exportVisits(visits)

                val path = visitsFileManager.getVisitsFilePath()
                _fileActionMessage.value = "Visits exported to file: $path"
            } catch (e: Exception) {
                _fileActionMessage.value = "Export error: ${e.message}"
            }
        }
    }

    fun importVisitsFromFile() {
        viewModelScope.launch {
            try {
                val importedVisits = visitsFileManager.readVisits()

                if (importedVisits.isEmpty()) {
                    _fileActionMessage.value = "File is empty or not found"
                    return@launch
                }

                val existingVisits = visitsDao.getAll()

                val existingKeys = existingVisits.map {
                    VisitUniqueKey(
                        centerId = it.center_id.trim(),
                        visitDate = it.visit_date,
                        comment = it.comment.normalizedComment()
                    )
                }.toMutableSet()

                val seenInFile = mutableSetOf<VisitUniqueKey>()
                val validNewVisits = mutableListOf<VisitEntity>()

                var invalidCount = 0
                var duplicateCount = 0

                for (visit in importedVisits) {
                    val normalizedCenterId = visit.center_id.trim()
                    val normalizedComment = visit.comment.normalizedComment()

                    val isValid =
                        normalizedCenterId.isNotEmpty() &&
                                visit.visit_date > 0 &&
                                visitsDao.centerExists(normalizedCenterId)

                    if (!isValid) {
                        invalidCount++
                        continue
                    }

                    val key = VisitUniqueKey(
                        centerId = normalizedCenterId,
                        visitDate = visit.visit_date,
                        comment = normalizedComment
                    )

                    if (key in seenInFile || key in existingKeys) {
                        duplicateCount++
                        continue
                    }

                    seenInFile.add(key)
                    existingKeys.add(key)

                    validNewVisits.add(
                        VisitEntity(
                            id = 0,
                            center_id = normalizedCenterId,
                            visit_date = visit.visit_date,
                            comment = normalizedComment
                        )
                    )
                }

                if (validNewVisits.isNotEmpty()) {
                    visitsDao.insertAll(validNewVisits)
                }

                _fileActionMessage.value =
                    "Imported: ${validNewVisits.size}, duplicates skipped: $duplicateCount, invalid skipped: $invalidCount"

            } catch (e: Exception) {
                _fileActionMessage.value = "Import error: ${e.message}"
            }
        }
    }

    fun deleteVisitsExportFile() {
        viewModelScope.launch {
            try {
                val deleted = visitsFileManager.deleteVisitsFile()
                _fileActionMessage.value = if (deleted) {
                    "Visits export file deleted"
                } else {
                    "File not found"
                }
            } catch (e: Exception) {
                _fileActionMessage.value = "Delete file error: ${e.message}"
            }
        }
    }

    fun clearFileActionMessage() {
        _fileActionMessage.value = null
    }

    private fun String?.normalizedComment(): String? {
        return this?.trim()?.takeIf { it.isNotEmpty() }
    }

    private data class VisitUniqueKey(
        val centerId: String,
        val visitDate: Long,
        val comment: String?
    )
}