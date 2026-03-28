package com.example.fitlife.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlife.data.local.DatabaseProvider
import com.example.fitlife.data.local.entity.FavoriteEntity
import com.example.fitlife.data.local.entity.VisitEntity
import com.example.fitlife.data.local.relation.FitnessCenterDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CenterDetailsViewModel(app: Application) : AndroidViewModel(app) {

    private val db = DatabaseProvider.get(app)
    private val centersDao = db.fitnessCenterDao()
    private val favoritesDao = db.favoritesDao()
    private val visitsDao = db.visitsDao()

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

    fun toggleFavorite(centerId: String, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            if (isCurrentlyFavorite) {
                // у твоєму FavoritesDao метод називається removeFromFavorites(centerId: String)
                favoritesDao.removeFromFavorites(centerId)
            } else {
                // у FavoriteEntity поле center_id — використовуємо іменований аргумент center_id
                favoritesDao.addToFavorites(
                    FavoriteEntity(
                        center_id = centerId,
                        addedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun addVisit(centerId: String, comment: String?) {
        viewModelScope.launch {
            visitsDao.insert(
                VisitEntity(
                    id = 0, // autoincrement
                    center_id = centerId,
                    visit_date = System.currentTimeMillis(),
                    comment = comment?.trim().takeIf { it?.isNotEmpty() == true }
                )
            )
        }
    }
}