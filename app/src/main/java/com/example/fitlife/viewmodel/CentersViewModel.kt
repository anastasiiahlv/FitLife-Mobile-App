package com.example.fitlife.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlife.data.local.DatabaseProvider
import com.example.fitlife.data.local.entity.FitnessCenterEntity
import com.example.fitlife.data.local.entity.TypeEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

class CentersViewModel(app: Application) : AndroidViewModel(app) {

    private val db = DatabaseProvider.get(app)
    private val fitnessCenterDao = db.fitnessCenterDao()
    private val typesDao = db.typesDao()

    // ------- Filters state -------
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedTypeId = MutableStateFlow<String?>(null)
    val selectedTypeId: StateFlow<String?> = _selectedTypeId

    private val _minRating = MutableStateFlow(0.0) // 0..5
    val minRating: StateFlow<Double> = _minRating

    private val _serviceQuery = MutableStateFlow("")
    val serviceQuery: StateFlow<String> = _serviceQuery

    private val _maxPriceText = MutableStateFlow("") // ввод користувача
    val maxPriceText: StateFlow<String> = _maxPriceText

    // ------- Types for dropdown -------
    val types: StateFlow<List<TypeEntity>> =
        typesDao.observeAll()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ------- Centers result -------
    val centers: StateFlow<List<FitnessCenterEntity>> =
        combine(
            _searchQuery,
            _selectedTypeId,
            _minRating,
            _serviceQuery,
            _maxPriceText
        ) { name, typeId, rating, service, maxPriceText ->
            val nameParam = name.trim().takeIf { it.isNotEmpty() }
            val serviceParam = service.trim().takeIf { it.isNotEmpty() }
            val maxPriceParam = maxPriceText.trim().toDoubleOrNull()
            val ratingParam = rating.takeIf { it > 0.0 }

            FilterParams(
                nameQuery = nameParam,
                typeId = typeId,
                minRating = ratingParam,
                serviceQuery = serviceParam,
                maxPrice = maxPriceParam
            )
        }.flatMapLatest { p ->
            fitnessCenterDao.observeFiltered(
                nameQuery = p.nameQuery,
                typeId = p.typeId,
                minRating = p.minRating,
                serviceQuery = p.serviceQuery,
                maxPrice = p.maxPrice
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ------- Update methods -------
    fun setSearchQuery(value: String) { _searchQuery.value = value }
    fun setSelectedTypeId(value: String?) { _selectedTypeId.value = value }
    fun setMinRating(value: Double) { _minRating.value = value }
    fun setServiceQuery(value: String) { _serviceQuery.value = value }
    fun setMaxPriceText(value: String) { _maxPriceText.value = value }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedTypeId.value = null
        _minRating.value = 0.0
        _serviceQuery.value = ""
        _maxPriceText.value = ""
    }

    private data class FilterParams(
        val nameQuery: String?,
        val typeId: String?,
        val minRating: Double?,
        val serviceQuery: String?,
        val maxPrice: Double?
    )
}
