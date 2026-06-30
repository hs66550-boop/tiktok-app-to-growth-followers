package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historical_metrics")
data class HistoricalMetric(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val timestamp: Long,
    val followerCount: Int,
    val likeCount: Long,
    val videoCount: Int
)
