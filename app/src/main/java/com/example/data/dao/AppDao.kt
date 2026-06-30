package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile)

    // Creator Accounts
    @Query("SELECT * FROM creator_accounts ORDER BY followerCount DESC")
    fun getAllCreatorAccountsFlow(): Flow<List<CreatorAccount>>

    @Query("SELECT * FROM creator_accounts WHERE username = :username LIMIT 1")
    suspend fun getCreatorAccount(username: String): CreatorAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreatorAccount(account: CreatorAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreatorAccounts(accounts: List<CreatorAccount>)

    @Query("DELETE FROM creator_accounts WHERE username = :username")
    suspend fun deleteCreatorAccount(username: String)

    // Historical Metrics
    @Query("SELECT * FROM historical_metrics WHERE username = :username ORDER BY timestamp ASC")
    fun getHistoricalMetricsFlow(username: String): Flow<List<HistoricalMetric>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoricalMetric(metric: HistoricalMetric)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoricalMetrics(metrics: List<HistoricalMetric>)

    // Growth Campaigns
    @Query("SELECT * FROM growth_campaigns ORDER BY timestamp DESC")
    fun getAllCampaignsFlow(): Flow<List<GrowthCampaign>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaign(campaign: GrowthCampaign)

    @Update
    suspend fun updateCampaign(campaign: GrowthCampaign)

    @Query("SELECT * FROM growth_campaigns WHERE status = 'ACTIVE' ORDER BY timestamp DESC")
    fun getActiveCampaignsFlow(): Flow<List<GrowthCampaign>>
}
