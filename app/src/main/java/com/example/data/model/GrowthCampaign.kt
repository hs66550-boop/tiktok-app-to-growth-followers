package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "growth_campaigns")
data class GrowthCampaign(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val creatorUsername: String, // e.g. "@alex_creates"
    val campaignType: String, // "FOLLOWERS" or "LIKES"
    val coinsSpent: Int,
    val targetCount: Int,
    val currentCount: Int,
    val coinsRewardPerAction: Int = 10,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE" // "ACTIVE" or "COMPLETED"
)
