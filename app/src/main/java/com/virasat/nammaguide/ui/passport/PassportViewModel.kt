package com.virasat.nammaguide.ui.passport

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.virasat.nammaguide.VGuideApplication
import com.virasat.nammaguide.data.db.entity.CheckIn
import com.virasat.nammaguide.data.db.entity.HeritageSite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class PassportEntry(val checkIn: CheckIn, val site: HeritageSite?)

class PassportViewModel(app: Application) : AndroidViewModel(app) {
    private val passportRepo = (app as VGuideApplication).passportRepository
    private val siteRepo = (app as VGuideApplication).siteRepository

    val totalCheckIns: Flow<Int> = passportRepo.totalCheckIns
    val totalSites: Flow<Int> = siteRepo.visitedCount

    val passportEntries: Flow<List<PassportEntry>> = combine(
        passportRepo.allCheckIns,
        siteRepo.allSites
    ) { checkIns: List<CheckIn>, sites: List<HeritageSite> ->
        checkIns.map { checkIn ->
            PassportEntry(checkIn, sites.find { site -> site.id == checkIn.siteId })
        }
    }
}
