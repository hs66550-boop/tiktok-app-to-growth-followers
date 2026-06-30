package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "creator_accounts")
data class CreatorAccount(
    @PrimaryKey val username: String, // e.g. "@khaby.lame"
    val displayName: String,
    val followerCount: Int,
    val followingCount: Int,
    val likeCount: Long,
    val videoCount: Int,
    val avatarUrl: String,
    val engagementRate: Float, // e.g. 6.2f for 6.2%
    val isTrackedByUser: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
)
