package com.virasat.nammaguide.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ai_query_cache",
    foreignKeys = [ForeignKey(
        entity = HeritageSite::class,
        parentColumns = ["id"],
        childColumns = ["siteId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("siteId")]
)
data class AIQueryCache(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val siteId: String,
    val question: String,
    val answer: String,
    val language: String,
    val timestamp: Long
)
