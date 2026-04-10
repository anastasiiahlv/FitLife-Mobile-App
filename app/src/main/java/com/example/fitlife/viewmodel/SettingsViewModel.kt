package com.example.fitlife.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {

    private val _selectedLanguage = MutableStateFlow(currentLanguage())
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    fun setLanguage(language: String) {
        val locales = when (language) {
            "en" -> LocaleListCompat.forLanguageTags("en")
            "uk" -> LocaleListCompat.forLanguageTags("uk")
            else -> LocaleListCompat.getEmptyLocaleList()
        }

        AppCompatDelegate.setApplicationLocales(locales)
        _selectedLanguage.value = language
    }

    private fun currentLanguage(): String {
        val tags = AppCompatDelegate.getApplicationLocales().toLanguageTags()

        return when {
            tags.startsWith("uk") -> "uk"
            tags.startsWith("en") -> "en"
            else -> "system"
        }
    }
}