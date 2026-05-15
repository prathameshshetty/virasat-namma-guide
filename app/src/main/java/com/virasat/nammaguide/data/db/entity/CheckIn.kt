package com.virasat.nammaguide.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "check_ins",
    foreignKeys = [ForeignKey(
        entity = HeritageSite::class,
        parentColumns = ["id"],
        childColumns = ["siteId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("siteId")]
)
data class CheckIn(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val siteId: String,
    val timestamp: Long,
    val method: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val checkOutTime: Long? = null
)
