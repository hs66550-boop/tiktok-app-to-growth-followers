package com.example.data.repository

import com.example.data.dao.AppDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class AppRepository(private val appDao: AppDao) {

    val userProfile: Flow<UserProfile?> = appDao.getUserProfileFlow()
    val creatorAccounts: Flow<List<CreatorAccount>> = appDao.getAllCreatorAccountsFlow()
    val activeCampaigns: Flow<List<GrowthCampaign>> = appDao.getActiveCampaignsFlow()
    val allCampaigns: Flow<List<GrowthCampaign>> = appDao.getAllCampaignsFlow()

    fun getHistoricalMetrics(username: String): Flow<List<HistoricalMetric>> {
        return appDao.getHistoricalMetricsFlow(username)
    }

    suspend fun getProfileSync(): UserProfile? {
        return appDao.getUserProfile()
    }

    suspend fun seedDatabaseIfNeeded() {
        val currentProfile = appDao.getUserProfile()
        if (currentProfile == null) {
            // Seed User Profile
            val defaultProfile = UserProfile(
                id = 1,
                username = "TikTok_Growth_Partner",
                linkedTikTokHandle = "@alex_creates",
                coins = 350,
                isAdmin = false,
                isPremium = false,
                completedActionsCount = 0,
                createdCampaignsCount = 0
            )
            appDao.insertUserProfile(defaultProfile)

            // Seed Tracked Creator Accounts
            val creators = listOf(
                CreatorAccount(
                    username = "@khaby.lame",
                    displayName = "Khaby Lame",
                    followerCount = 162150400,
                    followingCount = 78,
                    likeCount = 2400100200,
                    videoCount = 382,
                    avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
                    engagementRate = 6.4f
                ),
                CreatorAccount(
                    username = "@charli_d_amelio",
                    displayName = "Charli D'Amelio",
                    followerCount = 155310200,
                    followingCount = 1240,
                    likeCount = 11500300400,
                    videoCount = 1945,
                    avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=150&q=80",
                    engagementRate = 4.8f
                ),
                CreatorAccount(
                    username = "@zachking",
                    displayName = "Zach King",
                    followerCount = 92450300,
                    followingCount = 120,
                    likeCount = 1250200300,
                    videoCount = 492,
                    avatarUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&w=150&q=80",
                    engagementRate = 8.1f
                ),
                CreatorAccount(
                    username = "@bellapoarch",
                    displayName = "Bella Poarch",
                    followerCount = 94120800,
                    followingCount = 210,
                    likeCount = 2300450100,
                    videoCount = 524,
                    avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80",
                    engagementRate = 7.5f
                )
            )
            appDao.insertCreatorAccounts(creators)

            // Seed Historical Metrics for each creator (Last 7 days)
            val currentTime = System.currentTimeMillis()
            val oneDayMillis = 24 * 60 * 60 * 1000L

            for (creator in creators) {
                val baseFollowers = creator.followerCount - 150000
                val baseLikes = creator.likeCount - 500000
                val baseVideos = creator.videoCount - 5

                val historyList = mutableListOf<HistoricalMetric>()
                for (i in 0..6) {
                    val daysAgo = 6 - i
                    val timestamp = currentTime - (daysAgo * oneDayMillis)
                    // Gradual growth curve
                    val followersAtDay = baseFollowers + (i * 25000) + (Math.sin(i.toDouble()) * 5000).toInt()
                    val likesAtDay = baseLikes + (i * 83333)
                    val videosAtDay = baseVideos + (if (i % 2 == 0) 1 else 0)

                    historyList.add(
                        HistoricalMetric(
                            username = creator.username,
                            timestamp = timestamp,
                            followerCount = followersAtDay,
                            likeCount = likesAtDay,
                            videoCount = videosAtDay
                        )
                    )
                }
                appDao.insertHistoricalMetrics(historyList)
            }

            // Seed initial global campaign exchange listings (representing other creators running campaigns)
            val seededCampaigns = listOf(
                GrowthCampaign(
                    creatorUsername = "@travel_vlogger",
                    campaignType = "FOLLOWERS",
                    coinsSpent = 1200,
                    targetCount = 120,
                    currentCount = 43,
                    coinsRewardPerAction = 10,
                    timestamp = currentTime - oneDayMillis,
                    status = "ACTIVE"
                ),
                GrowthCampaign(
                    creatorUsername = "@gaming_pro_tt",
                    campaignType = "FOLLOWERS",
                    coinsSpent = 800,
                    targetCount = 80,
                    currentCount = 25,
                    coinsRewardPerAction = 10,
                    timestamp = currentTime - (2 * oneDayMillis),
                    status = "ACTIVE"
                ),
                GrowthCampaign(
                    creatorUsername = "@tok_makeup_queen",
                    campaignType = "LIKES",
                    coinsSpent = 600,
                    targetCount = 100,
                    currentCount = 76,
                    coinsRewardPerAction = 6,
                    timestamp = currentTime - 4 * 60 * 60 * 1000L,
                    status = "ACTIVE"
                )
            )
            for (c in seededCampaigns) {
                appDao.insertCampaign(c)
            }
        }
    }

    suspend fun updateLinkedTikTokHandle(handle: String) {
        val profile = appDao.getUserProfile() ?: UserProfile()
        val cleanedHandle = if (handle.startsWith("@")) handle else "@$handle"
        appDao.insertUserProfile(profile.copy(linkedTikTokHandle = cleanedHandle))
    }

    suspend fun trackNewCreator(username: String, displayName: String): Boolean {
        val cleanedUsername = if (username.startsWith("@")) username else "@$username"
        val existing = appDao.getCreatorAccount(cleanedUsername)
        if (existing != null) {
            appDao.insertCreatorAccount(existing.copy(isTrackedByUser = true))
            return true
        }

        // Simulate creating a new account with realistic starting data
        val randomFollowers = (1500..850000).random()
        val randomLikes = randomFollowers * (2L..12L).random()
        val randomVideos = (10..150).random()
        val randomEngagement = (2.1f + Math.random() * 8f).toFloat()

        val newAccount = CreatorAccount(
            username = cleanedUsername,
            displayName = displayName.ifEmpty { username },
            followerCount = randomFollowers,
            followingCount = (100..800).random(),
            likeCount = randomLikes,
            videoCount = randomVideos,
            avatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=150&q=80",
            engagementRate = Math.round(randomEngagement * 10f) / 10f,
            isTrackedByUser = true,
            lastUpdated = System.currentTimeMillis()
        )
        appDao.insertCreatorAccount(newAccount)

        // Seed 7 days of growth history
        val currentTime = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L
        val historyList = mutableListOf<HistoricalMetric>()
        var currentFollowers = randomFollowers - 4200
        var currentLikes = randomLikes - 12000

        for (i in 0..6) {
            val daysAgo = 6 - i
            val timestamp = currentTime - (daysAgo * oneDayMillis)
            val growth = (200..1200).random()
            val likeGrowth = growth * (2..5).random()
            currentFollowers += growth
            currentLikes += likeGrowth

            historyList.add(
                HistoricalMetric(
                    username = cleanedUsername,
                    timestamp = timestamp,
                    followerCount = currentFollowers,
                    likeCount = currentLikes,
                    videoCount = randomVideos
                )
            )
        }
        appDao.insertHistoricalMetrics(historyList)
        return true
    }

    suspend fun untrackCreator(username: String) {
        val account = appDao.getCreatorAccount(username)
        if (account != null) {
            // We can either delete it completely or mark it tracked = false
            // Let's delete it if it is not a pre-seeded megastar, or mark it tracked = false
            appDao.insertCreatorAccount(account.copy(isTrackedByUser = false))
        }
    }

    suspend fun purchaseCoins(amount: Int, priceCents: Int) {
        val profile = appDao.getUserProfile() ?: UserProfile()
        // Award coins
        appDao.insertUserProfile(profile.copy(coins = profile.coins + amount))
    }

    suspend fun toggleAdminMode(enable: Boolean) {
        val profile = appDao.getUserProfile() ?: UserProfile()
        val newCoins = if (enable) profile.coins + 500000 else profile.coins
        appDao.insertUserProfile(profile.copy(
            isAdmin = enable,
            isPremium = enable, // Admin automatically unlocks all premium capabilities!
            coins = if (enable) Math.max(newCoins, 999999) else profile.coins
        ))
    }

    suspend fun buyPremiumMembership() {
        val profile = appDao.getUserProfile() ?: UserProfile()
        appDao.insertUserProfile(profile.copy(isPremium = true))
    }

    suspend fun startCampaign(type: String, quantity: Int, coinsSpent: Int): Boolean {
        val profile = appDao.getUserProfile() ?: UserProfile()
        if (profile.coins < coinsSpent && !profile.isAdmin) {
            return false // Insufficient funds
        }

        // Deduct coins
        val newCoins = if (profile.isAdmin) profile.coins else profile.coins - coinsSpent
        appDao.insertUserProfile(profile.copy(
            coins = newCoins,
            createdCampaignsCount = profile.createdCampaignsCount + 1
        ))

        // Create campaign
        val campaign = GrowthCampaign(
            creatorUsername = profile.linkedTikTokHandle,
            campaignType = type,
            coinsSpent = coinsSpent,
            targetCount = quantity,
            currentCount = 0,
            coinsRewardPerAction = when(type) {
                "FOLLOWERS" -> 10
                else -> 6
            },
            status = "ACTIVE"
        )
        appDao.insertCampaign(campaign)
        return true
    }

    suspend fun completeActionForCampaign(campaignId: Int): Boolean {
        // User participates in another creator's campaign to earn coins
        val profile = appDao.getUserProfile() ?: UserProfile()
        val campaign = appDao.getAllCampaignsFlow().firstOrNull()?.find { it.id == campaignId } ?: return false

        if (campaign.status != "ACTIVE") return false

        // Increment user's coin count and actions count
        val reward = campaign.coinsRewardPerAction
        appDao.insertUserProfile(profile.copy(
            coins = profile.coins + reward,
            completedActionsCount = profile.completedActionsCount + 1
        ))

        // Update campaign progress
        val nextCount = campaign.currentCount + 1
        val isCompleted = nextCount >= campaign.targetCount
        appDao.updateCampaign(campaign.copy(
            currentCount = nextCount,
            status = if (isCompleted) "COMPLETED" else "ACTIVE"
        ))

        return true
    }

    suspend fun adminSimulateDelivery(campaignId: Int) {
        // Admin instantly delivers/finishes a campaign
        val campaign = appDao.getAllCampaignsFlow().firstOrNull()?.find { it.id == campaignId } ?: return
        appDao.updateCampaign(campaign.copy(
            currentCount = campaign.targetCount,
            status = "COMPLETED"
        ))
    }

    suspend fun addCustomCoins(amount: Int) {
        val profile = appDao.getUserProfile() ?: UserProfile()
        appDao.insertUserProfile(profile.copy(coins = profile.coins + amount))
    }
}
