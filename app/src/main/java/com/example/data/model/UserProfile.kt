package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val username: String = "TikTok_Growth_Partner",
    val linkedTikTokHandle: String = "@alex_creates",
    val coins: Int = 350,
    val isAdmin: Boolean = false,
    val isPremium: Boolean = false,
    val completedActionsCount: Int = 0,
    val createdCampaignsCount: Int = 0
)
