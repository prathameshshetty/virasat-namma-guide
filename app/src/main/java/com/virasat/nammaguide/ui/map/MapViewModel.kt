package com.virasat.nammaguide.ui.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.virasat.nammaguide.VGuideApplication

class MapViewModel(app: Application) : AndroidViewModel(app) {
    val allSites = (app as VGuideApplication).siteRepository.allSites
}
