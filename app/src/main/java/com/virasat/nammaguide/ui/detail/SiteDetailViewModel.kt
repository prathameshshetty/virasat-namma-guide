package com.virasat.nammaguide.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.virasat.nammaguide.VGuideApplication
import com.virasat.nammaguide.data.db.entity.HeritageSite
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val msg: String) : UiState<Nothing>()
}

class SiteDetailViewModel(app: Application) : AndroidViewModel(app) {
    private val siteRepo = (app as VGuideApplication).siteRepository
    private val passportRepo = (app as VGuideApplication).passportRepository

    private val _site = MutableStateFlow<UiState<HeritageSite>>(UiState.Loading)
    val site: StateFlow<UiState<HeritageSite>> = _site

    private val _description = MutableStateFlow<String>("")
    val description: StateFlow<String> = _description

    private val _checkInState = MutableStateFlow<String>("")
    val checkInState: StateFlow<String> = _checkInState

    private val _aiAnswer = MutableStateFlow<String>("")
    val aiAnswer: StateFlow<String> = _aiAnswer

    private val _isLoadingAI = MutableStateFlow(false)
    val isLoadingAI: StateFlow<Boolean> = _isLoadingAI

    private var currentSite: HeritageSite? = null

    fun loadSite(siteId: String, language: String) {
        viewModelScope.launch {
            try {
                val site = siteRepo.getSiteById(siteId)
                if (site != null) {
                    currentSite = site
                    _site.value = UiState.Success(site)
                    val desc = siteRepo.getOrFetchDescription(site, language)
                    _description.value = desc
                } else {
                    _site.value = UiState.Error("Site not found")
                }
            } catch (e: Exception) {
                _site.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun askGuide(question: String, language: String) {
        val site = currentSite ?: return
        viewModelScope.launch {
            _isLoadingAI.value = true
            val answer = siteRepo.askGuide(site.id, site.nameEn, question, language)
            _aiAnswer.value = answer
            _isLoadingAI.value = false
        }
    }

    fun checkInGPS(lat: Double, lon: Double) {
        val site = currentSite ?: return
        viewModelScope.launch {
            val dist = FloatArray(1)
            android.location.Location.distanceBetween(lat, lon, site.latitude, site.longitude, dist)
            if (dist[0] <= 200f) {
                val success = passportRepo.checkIn(site.id, "GPS", lat, lon)
                _checkInState.value = if (success) "checked_in" else "already_checked_in"
            } else {
                _checkInState.value = "too_far:${"%.0f".format(dist[0])}"
            }
        }
    }

    fun checkInQR() {
        val site = currentSite ?: return
        viewModelScope.launch {
            val success = passportRepo.checkIn(site.id, "QR")
            _checkInState.value = if (success) "checked_in" else "already_checked_in"
        }
    }
}
