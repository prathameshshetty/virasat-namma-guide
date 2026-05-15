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

    private val _checkOutState = MutableStateFlow<String>("")
    val checkOutState: StateFlow<String> = _checkOutState

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
                    // Only use cached description — never auto-call Gemini on page load
                    _description.value = site.description
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
            val answer = siteRepo.askGuide(site.id, site.nameEn, site.dynasty, site.period, question, language)
            _aiAnswer.value = answer
            _isLoadingAI.value = false
        }
    }

    fun checkInGPS(lat: Double, lon: Double) {
        val site = currentSite ?: return
        viewModelScope.launch {
            val success = passportRepo.checkIn(site.id, "GPS", lat, lon)
            _checkInState.value = if (success) "checked_in" else "already_checked_in"
        }
    }

    fun checkInQR() {
        val site = currentSite ?: return
        viewModelScope.launch {
            val success = passportRepo.checkIn(site.id, "QR")
            _checkInState.value = if (success) "checked_in" else "already_checked_in"
        }
    }

    fun checkOut() {
        val site = currentSite ?: return
        viewModelScope.launch {
            passportRepo.checkOut(site.id)
            val checkIn = passportRepo.getCheckIn(site.id)
            if (checkIn != null && checkIn.checkOutTime != null) {
                val durationMs = checkIn.checkOutTime - checkIn.timestamp
                val minutes = durationMs / 60000
                _checkOutState.value = "checked_out:$minutes"
            } else {
                _checkOutState.value = "checked_out:0"
            }
        }
    }

    fun loadCheckInStatus() {
        val site = currentSite ?: return
        viewModelScope.launch {
            val checkIn = passportRepo.getCheckIn(site.id)
            if (checkIn != null) {
                if (checkIn.checkOutTime != null) {
                    val durationMs = checkIn.checkOutTime - checkIn.timestamp
                    _checkInState.value = "already_checked_in"
                    _checkOutState.value = "checked_out:${durationMs / 60000}"
                } else {
                    _checkInState.value = "already_checked_in"
                }
            }
        }
    }
}
