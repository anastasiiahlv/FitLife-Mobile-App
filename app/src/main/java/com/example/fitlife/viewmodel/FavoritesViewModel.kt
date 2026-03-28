package com.example.fitlife.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlife.data.local.DatabaseProvider
import com.example.fitlife.data.local.entity.FitnessCenterEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel(app: Application) : AndroidViewModel(app) {

    private val db = DatabaseProvider.get(app)
    private val favoritesDao = db.favoritesDao()

    val favoriteCenters: StateFlow<List<FitnessCenterEntity>> =
        favoritesDao
            .observeFavoriteCenters()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}