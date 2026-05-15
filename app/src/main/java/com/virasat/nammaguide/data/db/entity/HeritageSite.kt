package com.virasat.nammaguide.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "heritage_sites")
data class HeritageSite(
    @PrimaryKey val id: String,
    val nameEn: String,
    val nameKn: String,
    val district: String,
    val dynasty: String,
    val period: String,
    val siteType: String,
    val latitude: Double,
    val longitude: Double,
    val description: String = "",
    val qrCode: String,
    val audioUrl: String = "",
    val isVisited: Boolean = false
)
