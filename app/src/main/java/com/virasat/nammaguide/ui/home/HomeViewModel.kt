package com.virasat.nammaguide.ui.home

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.virasat.nammaguide.VGuideApplication
import com.virasat.nammaguide.data.db.entity.HeritageSite
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

data class SiteWithDistance(val site: HeritageSite, val distanceM: Float)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = (app as VGuideApplication).siteRepository
    private val _location = MutableStateFlow<Location?>(null)
    private val _query = MutableStateFlow("")
    private val _filterType = MutableStateFlow<String?>(null)

    val sitesWithDistance: StateFlow<List<SiteWithDistance>> = combine(
        repo.allSites, _location
    ) { sites, loc ->
        sites.map { site ->
            val dist = if (loc != null) {
                val r = FloatArray(1)
                Location.distanceBetween(loc.latitude, loc.longitude, site.latitude, site.longitude, r)
                r[0]
            } else Float.MAX_VALUE
            SiteWithDistance(site, dist)
        }.sortedBy { it.distanceM }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredSites: StateFlow<List<SiteWithDistance>> = combine(
        sitesWithDistance, _query, _filterType
    ) { sites, q, type ->
        sites.filter { sd ->
            val matchQ = q.isBlank() || sd.site.nameEn.contains(q, true) || sd.site.nameKn.contains(q, true) || sd.site.district.contains(q, true)
            val matchT = type == null || sd.site.siteType == type
            matchQ && matchT
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateLocation(loc: Location) { _location.value = loc }
    fun search(q: String) { _query.value = q }
    fun filterByType(type: String?) { _filterType.value = type }
}
