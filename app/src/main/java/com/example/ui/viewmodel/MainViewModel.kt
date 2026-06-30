package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiService
import com.example.data.database.DatabaseProvider
import com.example.data.model.CreatorAccount
import com.example.data.model.GrowthCampaign
import com.example.data.model.HistoricalMetric
import com.example.data.model.UserProfile
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = DatabaseProvider.getDatabase(application)
    private val repository = AppRepository(database.appDao())
    private val geminiService = GeminiService()

    // Expose flows from Repository
    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val creatorAccounts: StateFlow<List<CreatorAccount>> = repository.creatorAccounts
        .map { list -> list.filter { it.isTrackedByUser } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCampaigns: StateFlow<List<GrowthCampaign>> = repository.activeCampaigns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCampaigns: StateFlow<List<GrowthCampaign>> = repository.allCampaigns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Creator details state
    private val _selectedCreator = MutableStateFlow<CreatorAccount?>(null)
    val selectedCreator: StateFlow<CreatorAccount?> = _selectedCreator.asStateFlow()

    private val _historicalMetrics = MutableStateFlow<List<HistoricalMetric>>(emptyList())
    val historicalMetrics: StateFlow<List<HistoricalMetric>> = _historicalMetrics.asStateFlow()

    // AI Feature States
    private val _aiAuditReport = MutableStateFlow<String>("")
    val aiAuditReport: StateFlow<String> = _aiAuditReport.asStateFlow()

    private val _aiScriptOutput = MutableStateFlow<String>("")
    val aiScriptOutput: StateFlow<String> = _aiScriptOutput.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    init {
        viewModelScope.launch {
            // Pre-populate database on first boot (creators, initial coin balance, history, etc.)
            repository.seedDatabaseIfNeeded()
            
            // Set first creator as default selection if any
            creatorAccounts.firstOrNull { it.isNotEmpty() }?.firstOrNull()?.let {
                selectCreator(it)
            }
        }
    }

    fun selectCreator(creator: CreatorAccount) {
        _selectedCreator.value = creator
        viewModelScope.launch {
            repository.getHistoricalMetrics(creator.username).collect { metrics ->
                _historicalMetrics.value = metrics
            }
        }
    }

    fun addCreatorTrack(username: String, displayName: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.trackNewCreator(username, displayName)
            if (success) {
                // Select the newly tracked creator
                val addedCreator = repository.creatorAccounts.firstOrNull()?.find { 
                    it.username.equals(username, ignoreCase = true) || 
                    it.username.equals("@$username", ignoreCase = true) 
                }
                if (addedCreator != null) {
                    selectCreator(addedCreator)
                }
            }
            onComplete(success)
        }
    }

    fun untrackCreator(username: String) {
        viewModelScope.launch {
            repository.untrackCreator(username)
            if (_selectedCreator.value?.username == username) {
                _selectedCreator.value = null
                _historicalMetrics.value = emptyList()
            }
        }
    }

    fun updateLinkedTikTok(handle: String) {
        viewModelScope.launch {
            repository.updateLinkedTikTokHandle(handle)
        }
    }

    fun startCampaign(type: String, quantity: Int, coinsSpent: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.startCampaign(type, quantity, coinsSpent)
            onComplete(success)
        }
    }

    fun completeAction(campaignId: Int) {
        viewModelScope.launch {
            repository.completeActionForCampaign(campaignId)
        }
    }

    fun purchaseCoins(amount: Int, priceCents: Int) {
        viewModelScope.launch {
            repository.purchaseCoins(amount, priceCents)
        }
    }

    fun toggleAdminMode(enable: Boolean) {
        viewModelScope.launch {
            repository.toggleAdminMode(enable)
        }
    }

    fun adminAddCoins(amount: Int) {
        viewModelScope.launch {
            repository.addCustomCoins(amount)
        }
    }

    fun adminSimulateDelivery(campaignId: Int) {
        viewModelScope.launch {
            repository.adminSimulateDelivery(campaignId)
        }
    }

    fun buyPremium() {
        viewModelScope.launch {
            repository.buyPremiumMembership()
        }
    }

    // AI Strategy Triggers using Gemini API
    fun runAiProfileAudit(username: String, followerCount: Int, engagementRate: Float) {
        _aiAuditReport.value = ""
        _isAiLoading.value = true
        viewModelScope.launch {
            val result = geminiService.generateGrowthStrategy(username, followerCount, engagementRate)
            _aiAuditReport.value = result
            _isAiLoading.value = false
        }
    }

    fun runAiScriptGenerator(topic: String, style: String) {
        _aiScriptOutput.value = ""
        _isAiLoading.value = true
        viewModelScope.launch {
            val result = geminiService.generateVideoScript(topic, style)
            _aiScriptOutput.value = result
            _isAiLoading.value = false
        }
    }
}
