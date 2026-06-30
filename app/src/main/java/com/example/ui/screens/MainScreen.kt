package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.CreatorAccount
import com.example.data.model.GrowthCampaign
import com.example.data.model.HistoricalMetric
import com.example.data.model.UserProfile
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val creatorAccounts by viewModel.creatorAccounts.collectAsStateWithLifecycle()
    val activeCampaigns by viewModel.activeCampaigns.collectAsStateWithLifecycle()
    val allCampaigns by viewModel.allCampaigns.collectAsStateWithLifecycle()
    val selectedCreator by viewModel.selectedCreator.collectAsStateWithLifecycle()
    val historicalMetrics by viewModel.historicalMetrics.collectAsStateWithLifecycle()
    
    // AI States
    val aiAuditReport by viewModel.aiAuditReport.collectAsStateWithLifecycle()
    val aiScriptOutput by viewModel.aiScriptOutput.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(0) }
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = TikTokCardDark,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = { Icon(if (currentTab == 0) Icons.Filled.Analytics else Icons.Outlined.Analytics, contentDescription = "Analytics") },
                    label = { Text("Analytics", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TikTokCyan,
                        selectedTextColor = TikTokCyan,
                        indicatorColor = TikTokPink.copy(alpha = 0.2f),
                        unselectedIconColor = TikTokGray,
                        unselectedTextColor = TikTokGray
                    ),
                    modifier = Modifier.testTag("nav_tab_analytics")
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = { Icon(if (currentTab == 1) Icons.Filled.TrendingUp else Icons.Outlined.TrendingUp, contentDescription = "Grow Hub") },
                    label = { Text("Grow Hub", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TikTokPink,
                        selectedTextColor = TikTokPink,
                        indicatorColor = TikTokCyan.copy(alpha = 0.2f),
                        unselectedIconColor = TikTokGray,
                        unselectedTextColor = TikTokGray
                    ),
                    modifier = Modifier.testTag("nav_tab_grow")
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = { Icon(if (currentTab == 2) Icons.Filled.Psychology else Icons.Outlined.Psychology, contentDescription = "Creator AI") },
                    label = { Text("Creator AI", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TikTokCyan,
                        selectedTextColor = TikTokCyan,
                        indicatorColor = TikTokPink.copy(alpha = 0.2f),
                        unselectedIconColor = TikTokGray,
                        unselectedTextColor = TikTokGray
                    ),
                    modifier = Modifier.testTag("nav_tab_ai")
                )
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3 },
                    icon = { Icon(if (currentTab == 3) Icons.Filled.LocalMall else Icons.Outlined.LocalMall, contentDescription = "Shop") },
                    label = { Text("Shop & Admin", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TikTokGold,
                        selectedTextColor = TikTokGold,
                        indicatorColor = TikTokGold.copy(alpha = 0.2f),
                        unselectedIconColor = TikTokGray,
                        unselectedTextColor = TikTokGray
                    ),
                    modifier = Modifier.testTag("nav_tab_shop")
                )
            }
        },
        containerColor = TikTokBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> AnalyticsTab(
                    viewModel = viewModel,
                    creatorAccounts = creatorAccounts,
                    selectedCreator = selectedCreator,
                    historicalMetrics = historicalMetrics,
                    userProfile = userProfile
                )
                1 -> GrowHubTab(
                    viewModel = viewModel,
                    userProfile = userProfile,
                    allCampaigns = allCampaigns
                )
                2 -> CreatorAiTab(
                    viewModel = viewModel,
                    selectedCreator = selectedCreator,
                    aiAuditReport = aiAuditReport,
                    aiScriptOutput = aiScriptOutput,
                    isAiLoading = isAiLoading,
                    userProfile = userProfile
                )
                3 -> ShopAdminTab(
                    viewModel = viewModel,
                    userProfile = userProfile,
                    allCampaigns = allCampaigns
                )
            }
        }
    }
}

// ==========================================
// 1. ANALYTICS TAB
// ==========================================
@Composable
fun AnalyticsTab(
    viewModel: MainViewModel,
    creatorAccounts: List<CreatorAccount>,
    selectedCreator: CreatorAccount?,
    historicalMetrics: List<HistoricalMetric>,
    userProfile: UserProfile?
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var inputUsername by remember { mutableStateOf("") }
    var inputDisplayName by remember { mutableStateOf("") }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "TikToker",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TikTokWhite
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ace",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TikTokPink
                        )
                    }
                    Text(
                        text = "Real-time TikTok Analytics",
                        fontSize = 14.sp,
                        color = TikTokGray
                    )
                }

                // Balance Chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(TikTokCardDark)
                        .border(1.dp, TikTokGold.copy(alpha = 0.5f), RoundedCornerShape(50.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.MonetizationOn,
                        contentDescription = "Coins",
                        tint = TikTokGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${userProfile?.coins ?: 0}",
                        color = TikTokWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    if (userProfile?.isAdmin == true) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(TikTokPink)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("ADMIN", color = TikTokWhite, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Account Selector Strip
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tracked Profiles",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TikTokWhite
                    )
                    
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("add_creator_button")
                    ) {
                        Icon(Icons.Filled.AddCircle, contentDescription = "Add Profile", tint = TikTokCyan, modifier = Modifier.size(28.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                if (creatorAccounts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(TikTokCardDark, RoundedCornerShape(12.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.People, contentDescription = null, tint = TikTokGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No profiles tracked yet.", color = TikTokWhite, fontWeight = FontWeight.Medium)
                            Text("Click the + icon to track a profile.", color = TikTokGray, fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        creatorAccounts.forEach { creator ->
                            val isSelected = selectedCreator?.username == creator.username
                            Card(
                                modifier = Modifier
                                    .width(120.dp)
                                    .clickable { viewModel.selectCreator(creator) }
                                    .testTag("creator_chip_${creator.username}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) TikTokPink.copy(alpha = 0.2f) else TikTokCardDark
                                ),
                                border = if (isSelected) BorderStroke(1.5.dp, TikTokPink) else null,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AsyncImage(
                                        model = creator.avatarUrl,
                                        contentDescription = creator.displayName,
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, if (isSelected) TikTokPink else TikTokCyan, CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = creator.displayName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TikTokWhite,
                                        maxLines = 1,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = creator.username,
                                        fontSize = 10.sp,
                                        color = TikTokGray,
                                        maxLines = 1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected Creator Real-time Dashboard
        if (selectedCreator != null) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Profile Header & Untrack button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Analytics for ${selectedCreator.displayName}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TikTokWhite
                            )
                        }
                        
                        TextButton(
                            onClick = { viewModel.untrackCreator(selectedCreator.username) },
                            colors = ButtonDefaults.textButtonColors(contentColor = TikTokPink)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Stop Tracking", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Stop Tracking", fontSize = 12.sp)
                        }
                    }

                    // Key Metrics Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricCard(
                            title = "Followers",
                            value = formatNumber(selectedCreator.followerCount),
                            subText = "+12.4K today",
                            subTextColor = TikTokGreen,
                            icon = Icons.Filled.People,
                            iconColor = TikTokCyan,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Likes Received",
                            value = formatNumber(selectedCreator.likeCount),
                            subText = "Authentic Growth",
                            subTextColor = TikTokGray,
                            icon = Icons.Filled.Favorite,
                            iconColor = TikTokPink,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricCard(
                            title = "Videos",
                            value = selectedCreator.videoCount.toString(),
                            subText = "Active Content",
                            subTextColor = TikTokGray,
                            icon = Icons.Filled.Videocam,
                            iconColor = TikTokWhite,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Engagement",
                            value = "${selectedCreator.engagementRate}%",
                            subText = "Very Strong",
                            subTextColor = TikTokGreen,
                            icon = Icons.Filled.Percent,
                            iconColor = TikTokGold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Follower Growth Chart Header
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = TikTokCardDark),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Follower Growth Chart",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TikTokWhite
                                    )
                                    Text(
                                        text = "Real-time daily growth curve",
                                        fontSize = 12.sp,
                                        color = TikTokGray
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(TikTokCyan.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("LIVE", color = TikTokCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))

                            if (historicalMetrics.isNotEmpty()) {
                                GrowthChart(
                                    metrics = historicalMetrics,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No metrics points loaded.", color = TikTokGray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Profile Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Track New TikTok Creator", color = TikTokWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Enter the creator's username and display name to track their metrics in real-time.", color = TikTokGray, fontSize = 14.sp)
                    
                    OutlinedTextField(
                        value = inputUsername,
                        onValueChange = { inputUsername = it },
                        label = { Text("TikTok Username") },
                        placeholder = { Text("@khaby.lame") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TikTokCyan,
                            unfocusedBorderColor = TikTokGray,
                            focusedLabelColor = TikTokCyan,
                            unfocusedLabelColor = TikTokGray,
                            focusedTextColor = TikTokWhite,
                            unfocusedTextColor = TikTokWhite
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("add_username_input")
                    )

                    OutlinedTextField(
                        value = inputDisplayName,
                        onValueChange = { inputDisplayName = it },
                        label = { Text("Display Name") },
                        placeholder = { Text("Khaby Lame") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TikTokCyan,
                            unfocusedBorderColor = TikTokGray,
                            focusedLabelColor = TikTokCyan,
                            unfocusedLabelColor = TikTokGray,
                            focusedTextColor = TikTokWhite,
                            unfocusedTextColor = TikTokWhite
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("add_display_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputUsername.isEmpty()) {
                            Toast.makeText(context, "Username cannot be empty", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.addCreatorTrack(inputUsername, inputDisplayName) { success ->
                            if (success) {
                                Toast.makeText(context, "Now tracking @$inputUsername!", Toast.LENGTH_SHORT).show()
                                showAddDialog = false
                                inputUsername = ""
                                inputDisplayName = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TikTokCyan, contentColor = TikTokBlack)
                ) {
                    Text("Track Profile", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = TikTokGray)
                }
            },
            containerColor = TikTokCardDark
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subText: String,
    subTextColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = TikTokCardDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = TikTokGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = TikTokWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subText, color = subTextColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// Gorgeous Custom Canvas growth chart!
@Composable
fun GrowthChart(metrics: List<HistoricalMetric>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        if (metrics.size < 2) return@Canvas

        val maxFollowers = metrics.maxOf { it.followerCount }.toDouble()
        val minFollowers = metrics.minOf { it.followerCount }.toDouble()
        val followersRange = maxFollowers - minFollowers
        val safeRange = if (followersRange == 0.0) 1.0 else followersRange

        val xSpacing = size.width / (metrics.size - 1)
        val points = metrics.mapIndexed { index, metric ->
            val fraction = (metric.followerCount - minFollowers) / safeRange
            val x = index * xSpacing
            // Invert Y so higher is on top
            val y = size.height - (fraction.toFloat() * (size.height - 40.dp.toPx())) - 20.dp.toPx()
            Offset(x, y)
        }

        // Draw Chart Grid lines
        val gridLines = 3
        val gridYSpacing = size.height / (gridLines + 1)
        for (i in 1..gridLines) {
            val y = i * gridYSpacing
            drawLine(
                color = TikTokGray.copy(alpha = 0.15f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Build elegant line path
        val strokePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                val p0 = points[i - 1]
                val p1 = points[i]
                // Draw bezier control points for curved, smooth growth representation
                val cpX1 = p0.x + (p1.x - p0.x) / 2f
                val cpY1 = p0.y
                val cpX2 = p0.x + (p1.x - p0.x) / 2f
                val cpY2 = p1.y
                cubicTo(cpX1, cpY1, cpX2, cpY2, p1.x, p1.y)
            }
        }

        // Build filled gradient path under the curve
        val fillPath = Path().apply {
            addPath(strokePath)
            lineTo(points.last().x, size.height)
            lineTo(points.first().x, size.height)
            close()
        }

        // Draw fill gradient under the line
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(TikTokCyan.copy(alpha = 0.35f), Color.Transparent),
                startY = points.minOf { it.y },
                endY = size.height
            )
        )

        // Draw line stroke
        drawPath(
            path = strokePath,
            color = TikTokCyan,
            style = Stroke(width = 3.dp.toPx())
        )

        // Draw point dots
        points.forEachIndexed { index, point ->
            // Outer dot circle
            drawCircle(
                color = TikTokPink,
                radius = 5.dp.toPx(),
                center = point
            )
            // Inner dot center
            drawCircle(
                color = TikTokWhite,
                radius = 2.dp.toPx(),
                center = point
            )
        }
    }
}


// ==========================================
// 2. GROW HUB TAB (Campaigns & Exchange)
// ==========================================
@Composable
fun GrowHubTab(
    viewModel: MainViewModel,
    userProfile: UserProfile?,
    allCampaigns: List<GrowthCampaign>
) {
    val context = LocalContext.current
    var showLinkDialog by remember { mutableStateOf(false) }
    var showCampaignDialog by remember { mutableStateOf(false) }
    var tikTokHandleInput by remember { mutableStateOf("") }
    
    // Campaign form states
    var selectedCampaignType by remember { mutableStateOf("FOLLOWERS") }
    var selectedQuantity by remember { mutableStateOf(100) }
    
    val coinsRequired = when (selectedCampaignType) {
        "FOLLOWERS" -> selectedQuantity * 10
        else -> selectedQuantity * 6
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Grow Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Grow Network",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TikTokWhite
                    )

                    // Linked Handle Status
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(TikTokCardDark)
                            .clickable { showLinkDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Link, contentDescription = null, tint = TikTokCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = userProfile?.linkedTikTokHandle ?: "Link Account",
                            color = if (userProfile?.linkedTikTokHandle == null) TikTokPink else TikTokWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Text(
                    text = "Gain real authentic organic followers, no bots!",
                    fontSize = 13.sp,
                    color = TikTokGray
                )
            }
        }

        // Main Marketing Pitch Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = TikTokCardDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(TikTokPink.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = TikTokPink, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("100% Real Active Followers Only", color = TikTokWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Our platform works on a reciprocal mutual-exchange creator ecosystem. Real active users complete campaigns to earn coins. Your profile is promoted directly inside the feed. Zero bots, zero fake drop-offs. Genuine engagement guaranteed.",
                        fontSize = 12.sp,
                        color = TikTokGray,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (userProfile?.linkedTikTokHandle == null || userProfile.linkedTikTokHandle.isEmpty()) {
                                Toast.makeText(context, "Please link your TikTok handle in the top bar first!", Toast.LENGTH_LONG).show()
                                showLinkDialog = true
                            } else {
                                showCampaignDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TikTokPink, contentColor = TikTokWhite),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("launch_campaign_button")
                    ) {
                        Icon(Icons.Filled.RocketLaunch, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create Follower Campaign", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        // Live Exchange Directory (Earn Coins)
        item {
            Text(
                text = "Organic Exchange Directory (Earn Coins)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TikTokWhite
            )
        }

        // Campaign Items List
        val activeCampaignExchanges = allCampaigns.filter { it.status == "ACTIVE" }
        if (activeCampaignExchanges.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TikTokCardDark, RoundedCornerShape(12.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Campaign, contentDescription = null, tint = TikTokGray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("All campaigns fully completed!", color = TikTokWhite, fontWeight = FontWeight.Medium)
                        Text("Check back later or launch your own campaign.", color = TikTokGray, fontSize = 12.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            items(activeCampaignExchanges) { campaign ->
                ExchangeCard(
                    campaign = campaign,
                    onActionClick = {
                        viewModel.completeAction(campaign.id)
                        Toast.makeText(context, "Verifying Action... Earned +${campaign.coinsRewardPerAction} Creator Coins!", Toast.LENGTH_SHORT).show()
                    },
                    isAdmin = userProfile?.isAdmin == true,
                    onAdminSimulate = {
                        viewModel.adminSimulateDelivery(campaign.id)
                        Toast.makeText(context, "Admin: Campaign results instantly delivered!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    // Link Account Dialog
    if (showLinkDialog) {
        AlertDialog(
            onDismissRequest = { showLinkDialog = false },
            title = { Text("Link Your TikTok Account", color = TikTokWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Enter your TikTok handle below. We promote this handle across our creator community network.", color = TikTokGray, fontSize = 14.sp)
                    OutlinedTextField(
                        value = tikTokHandleInput,
                        onValueChange = { tikTokHandleInput = it },
                        label = { Text("TikTok Handle") },
                        placeholder = { Text("@alex_creates") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TikTokCyan,
                            unfocusedBorderColor = TikTokGray,
                            focusedLabelColor = TikTokCyan,
                            unfocusedLabelColor = TikTokGray,
                            focusedTextColor = TikTokWhite,
                            unfocusedTextColor = TikTokWhite
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("link_handle_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tikTokHandleInput.isNotEmpty()) {
                            viewModel.updateLinkedTikTok(tikTokHandleInput)
                            Toast.makeText(context, "Account linked successfully!", Toast.LENGTH_SHORT).show()
                            showLinkDialog = false
                            tikTokHandleInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TikTokCyan, contentColor = TikTokBlack)
                ) {
                    Text("Link Account", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLinkDialog = false }) {
                    Text("Cancel", color = TikTokGray)
                }
            },
            containerColor = TikTokCardDark
        )
    }

    // Launch Campaign Dialog
    if (showCampaignDialog) {
        AlertDialog(
            onDismissRequest = { showCampaignDialog = false },
            title = { Text("Launch Growth Campaign", color = TikTokWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Promote ${userProfile?.linkedTikTokHandle} organically to real creators.", color = TikTokGray, fontSize = 13.sp)
                    
                    // Campaign Type Selection
                    Column {
                        Text("Campaign Objective:", color = TikTokWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedCampaignType = "FOLLOWERS" },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedCampaignType == "FOLLOWERS") TikTokPink.copy(alpha = 0.2f) else TikTokCardDark
                                ),
                                border = if (selectedCampaignType == "FOLLOWERS") BorderStroke(1.5.dp, TikTokPink) else BorderStroke(1.dp, TikTokGray.copy(alpha = 0.3f))
                            ) {
                                Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                                    Text("Organic Followers", color = TikTokWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedCampaignType = "LIKES" },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedCampaignType == "LIKES") TikTokCyan.copy(alpha = 0.2f) else TikTokCardDark
                                ),
                                border = if (selectedCampaignType == "LIKES") BorderStroke(1.5.dp, TikTokCyan) else BorderStroke(1.dp, TikTokGray.copy(alpha = 0.3f))
                            ) {
                                Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                                    Text("Organic Likes", color = TikTokWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Package Selection
                    Column {
                        Text("Select Target Size:", color = TikTokWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val packages = listOf(50, 100, 250, 500)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            packages.forEach { qty ->
                                val isSelected = selectedQuantity == qty
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedQuantity = qty },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) TikTokGold.copy(alpha = 0.2f) else TikTokCardDark
                                    ),
                                    border = if (isSelected) BorderStroke(1.5.dp, TikTokGold) else BorderStroke(1.dp, TikTokGray.copy(alpha = 0.2f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("+$qty", color = TikTokWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = if (selectedCampaignType == "FOLLOWERS") "${qty * 10}" else "${qty * 6}",
                                            color = TikTokGold,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Price Summary
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = TikTokBlack),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Cost Summary", color = TikTokGray, fontSize = 11.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = TikTokGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("$coinsRequired Creator Coins", color = TikTokWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                            
                            val userCoins = userProfile?.coins ?: 0
                            val balanceAfter = userCoins - coinsRequired
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Your Wallet Balance", color = TikTokGray, fontSize = 11.sp)
                                Text(
                                    text = "$userCoins Coins",
                                    color = if (userCoins >= coinsRequired || userProfile?.isAdmin == true) TikTokGreen else TikTokPink,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                val userCoins = userProfile?.coins ?: 0
                val canAfford = userCoins >= coinsRequired || userProfile?.isAdmin == true
                
                Button(
                    onClick = {
                        if (!canAfford) {
                            Toast.makeText(context, "Insufficient Creator Coins! Purchase more or earn some for free below.", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                        viewModel.startCampaign(selectedCampaignType, selectedQuantity, coinsRequired) { success ->
                            if (success) {
                                Toast.makeText(context, "Campaign launched organically in active feed!", Toast.LENGTH_LONG).show()
                                showCampaignDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canAfford) TikTokPink else TikTokGray,
                        contentColor = TikTokWhite
                    ),
                    modifier = Modifier.testTag("submit_campaign_button")
                ) {
                    Text("Launch Now", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCampaignDialog = false }) {
                    Text("Cancel", color = TikTokGray)
                }
            },
            containerColor = TikTokCardDark
        )
    }
}

@Composable
fun ExchangeCard(
    campaign: GrowthCampaign,
    onActionClick: () -> Unit,
    isAdmin: Boolean = false,
    onAdminSimulate: () -> Unit = {}
) {
    val progress = campaign.currentCount.toFloat() / campaign.targetCount
    val progressPercent = (progress * 100).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TikTokCardDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(TikTokPink.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = campaign.creatorUsername.take(2).uppercase(),
                            color = TikTokPink,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = campaign.creatorUsername,
                            color = TikTokWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Objective: ${campaign.campaignType}",
                            color = TikTokGray,
                            fontSize = 11.sp
                        )
                    }
                }

                Button(
                    onClick = onActionClick,
                    colors = ButtonDefaults.buttonColors(containerColor = TikTokCyan, contentColor = TikTokBlack),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Filled.GroupAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+${campaign.coinsRewardPerAction} Coins",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Organic Progress:", color = TikTokGray, fontSize = 11.sp)
                Text("${campaign.currentCount}/${campaign.targetCount} ($progressPercent%)", color = TikTokCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = TikTokCyan,
                trackColor = TikTokBlack
            )

            if (isAdmin) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onAdminSimulate,
                        colors = ButtonDefaults.buttonColors(containerColor = TikTokGold, contentColor = TikTokBlack),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text("Admin: Deliver Instantly", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// ==========================================
// 3. CREATOR AI TAB (Gemini-powered tools)
// ==========================================
@Composable
fun CreatorAiTab(
    viewModel: MainViewModel,
    selectedCreator: CreatorAccount?,
    aiAuditReport: String,
    aiScriptOutput: String,
    isAiLoading: Boolean,
    userProfile: UserProfile?
) {
    var aiTabSelected by remember { mutableStateOf(0) }
    var scriptTopicInput by remember { mutableStateOf("") }
    var scriptStyleSelected by remember { mutableStateOf("High Energy Hook") }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Hub Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Creator AI Assistant",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TikTokWhite
                    )
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(TikTokCyan.copy(alpha = 0.2f))
                            .border(1.dp, TikTokCyan, RoundedCornerShape(50.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("GEMINI AI", color = TikTokCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = "Generate scripts, ideas, and audits in seconds.",
                    fontSize = 13.sp,
                    color = TikTokGray
                )
            }
        }

        // Inner Sub-navigation (Audit vs Script)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { aiTabSelected = 0 },
                    colors = CardDefaults.cardColors(
                        containerColor = if (aiTabSelected == 0) TikTokCyan.copy(alpha = 0.2f) else TikTokCardDark
                    ),
                    border = if (aiTabSelected == 0) BorderStroke(1.5.dp, TikTokCyan) else null
                ) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = TikTokCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Profile AI Audit", color = TikTokWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { aiTabSelected = 1 },
                    colors = CardDefaults.cardColors(
                        containerColor = if (aiTabSelected == 1) TikTokPink.copy(alpha = 0.2f) else TikTokCardDark
                    ),
                    border = if (aiTabSelected == 1) BorderStroke(1.5.dp, TikTokPink) else null
                ) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Edit, contentDescription = null, tint = TikTokPink, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Viral Script Generator", color = TikTokWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Feature views
        if (aiTabSelected == 0) {
            // PROFILE AI AUDIT
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TikTokCardDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Audit Tracked Creator Page", color = TikTokWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Analyze metrics, calculate viral coefficients, and receive an actionable growth blueprint curated by Gemini.",
                            fontSize = 12.sp,
                            color = TikTokGray
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        if (selectedCreator == null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(TikTokBlack, RoundedCornerShape(10.dp))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Select a creator from the Analytics tab to run an AI Audit.", color = TikTokGray, fontSize = 12.sp, textAlign = TextAlign.Center)
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(TikTokBlack, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = selectedCreator.avatarUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, TikTokCyan, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(selectedCreator.displayName, color = TikTokWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("${formatNumber(selectedCreator.followerCount)} Followers | ${selectedCreator.engagementRate}% Engagement", color = TikTokGray, fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    viewModel.runAiProfileAudit(
                                        selectedCreator.username,
                                        selectedCreator.followerCount,
                                        selectedCreator.engagementRate
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TikTokCyan, contentColor = TikTokBlack),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("run_audit_button")
                            ) {
                                if (isAiLoading) {
                                    CircularProgressIndicator(color = TikTokBlack, modifier = Modifier.size(24.dp))
                                } else {
                                    Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Analyze with Gemini AI", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            if (aiAuditReport.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = TikTokCardDark),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Gemini Audit Strategy Blueprint", color = TikTokCyan, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                IconButton(onClick = {
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, aiAuditReport)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, null)
                                    context.startActivity(shareIntent)
                                }) {
                                    Icon(Icons.Filled.Share, contentDescription = "Share", tint = TikTokWhite)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = TikTokGray.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = aiAuditReport,
                                color = TikTokWhite,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        } else {
            // VIRAL SCRIPT GENERATOR
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TikTokCardDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Generate TikTok Video Script", color = TikTokWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Get a retention-engineered script structure containing hook, video directives, and optimized viral tags.",
                            fontSize = 12.sp,
                            color = TikTokGray
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = scriptTopicInput,
                            onValueChange = { scriptTopicInput = it },
                            label = { Text("Video Topic") },
                            placeholder = { Text("How to get more views / day in life of programmer...") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TikTokPink,
                                unfocusedBorderColor = TikTokGray,
                                focusedLabelColor = TikTokPink,
                                unfocusedLabelColor = TikTokGray,
                                focusedTextColor = TikTokWhite,
                                unfocusedTextColor = TikTokWhite
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("ai_topic_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Select Vibe/Style:", color = TikTokWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        val scriptStyles = listOf("High Energy Hook", "Detailed Tutorial", "Storytelling", "Humor Vibe")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            scriptStyles.forEach { style ->
                                val isSelected = scriptStyleSelected == style
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) TikTokPink.copy(alpha = 0.2f) else TikTokBlack)
                                        .border(1.dp, if (isSelected) TikTokPink else TikTokGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .clickable { scriptStyleSelected = style }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(style, color = TikTokWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (scriptTopicInput.isEmpty()) {
                                    Toast.makeText(context, "Please write a topic!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                viewModel.runAiScriptGenerator(scriptTopicInput, scriptStyleSelected)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TikTokPink, contentColor = TikTokWhite),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("generate_script_button")
                        ) {
                            if (isAiLoading) {
                                CircularProgressIndicator(color = TikTokWhite, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate Viral Script", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            if (aiScriptOutput.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = TikTokCardDark),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Your Viral Video Script", color = TikTokPink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                IconButton(onClick = {
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, aiScriptOutput)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, null)
                                    context.startActivity(shareIntent)
                                }) {
                                    Icon(Icons.Filled.Share, contentDescription = "Share", tint = TikTokWhite)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = TikTokGray.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = aiScriptOutput,
                                color = TikTokWhite,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 4. SHOP & ADMIN CONTROLS TAB
// ==========================================
@Composable
fun ShopAdminTab(
    viewModel: MainViewModel,
    userProfile: UserProfile?,
    allCampaigns: List<GrowthCampaign>
) {
    val context = LocalContext.current
    var adminCustomCoinsStr by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Shop Header
        item {
            Column {
                Text(
                    text = "Coin Shop",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TikTokWhite
                )
                Text(
                    text = "Refill creator coins cheap. Fuel organic growth campaigns.",
                    fontSize = 13.sp,
                    color = TikTokGray
                )
            }
        }

        // Premium Wallet Info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = TikTokCardDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Current Balance", color = TikTokGray, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = TikTokGold, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${userProfile?.coins ?: 0}",
                                color = TikTokWhite,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (userProfile?.isPremium == true) "Premium Account Unlocked 👑" else "Free Creator tier",
                            color = if (userProfile?.isPremium == true) TikTokGold else TikTokGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (userProfile?.isPremium == false) {
                        Button(
                            onClick = {
                                viewModel.buyPremium()
                                Toast.makeText(context, "Unlocked Premium Access! Priority delivery activated.", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TikTokGold, contentColor = TikTokBlack)
                        ) {
                            Text("Go Premium", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Cheap Coin Purchase Packages list
        item {
            Text("Refill Packages (Organic Rates)", color = TikTokWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ShopItemRow(
                    coins = 1000,
                    price = "$1.99",
                    badgeText = "Best for starters",
                    onBuyClick = {
                        viewModel.purchaseCoins(1000, 199)
                        Toast.makeText(context, "Added 1,000 Creator Coins to balance!", Toast.LENGTH_SHORT).show()
                    }
                )
                ShopItemRow(
                    coins = 5000,
                    price = "$4.99",
                    badgeText = "Most Popular!",
                    badgeColor = TikTokPink,
                    onBuyClick = {
                        viewModel.purchaseCoins(5000, 499)
                        Toast.makeText(context, "Added 5,000 Creator Coins to balance!", Toast.LENGTH_SHORT).show()
                    }
                )
                ShopItemRow(
                    coins = 20000,
                    price = "$14.99",
                    badgeText = "Best Growth Rate!",
                    badgeColor = TikTokCyan,
                    onBuyClick = {
                        viewModel.purchaseCoins(20000, 1499)
                        Toast.makeText(context, "Added 20,000 Creator Coins to balance!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // ADMIN CONTROLS UNIT (Visible to everyone for sandbox ease)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, TikTokPink, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = TikTokCardDark.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Build, contentDescription = null, tint = TikTokPink, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Admin Suite (Testing)", color = TikTokWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Switch(
                            checked = userProfile?.isAdmin == true,
                            onCheckedChange = { enable ->
                                viewModel.toggleAdminMode(enable)
                                Toast.makeText(
                                    context,
                                    if (enable) "Admin Mode Activated! Unlimited Premium Features Unlocked." else "Admin Mode Deactivated.",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TikTokPink,
                                checkedTrackColor = TikTokPink.copy(alpha = 0.4f),
                                uncheckedThumbColor = TikTokGray,
                                uncheckedTrackColor = TikTokBlack
                            ),
                            modifier = Modifier.testTag("admin_toggle_switch")
                        )
                    }

                    Text(
                        text = "Toggle Admin Mode to grant yourself unlimited testing coins (+999K) and unlock every premium AI profile report.",
                        fontSize = 11.sp,
                        color = TikTokGray,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    if (userProfile?.isAdmin == true) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = TikTokGray.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Admin Coins Grant
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = adminCustomCoinsStr,
                                onValueChange = { adminCustomCoinsStr = it },
                                label = { Text("Grant Coins", fontSize = 11.sp) },
                                placeholder = { Text("10000") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TikTokGold,
                                    unfocusedBorderColor = TikTokGray,
                                    focusedLabelColor = TikTokGold,
                                    unfocusedLabelColor = TikTokGray,
                                    focusedTextColor = TikTokWhite,
                                    unfocusedTextColor = TikTokWhite
                                ),
                                modifier = Modifier.weight(1f).height(50.dp)
                            )

                            Button(
                                onClick = {
                                    val qty = adminCustomCoinsStr.toIntOrNull() ?: 10000
                                    viewModel.adminAddCoins(qty)
                                    Toast.makeText(context, "Granted $qty Coins!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TikTokGold, contentColor = TikTokBlack),
                                modifier = Modifier.height(50.dp)
                            ) {
                                Text("Add", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Complete active campaigns list
                        Button(
                            onClick = {
                                val active = allCampaigns.filter { it.status == "ACTIVE" }
                                if (active.isEmpty()) {
                                    Toast.makeText(context, "No active campaigns to simulate delivery for.", Toast.LENGTH_SHORT).show()
                                } else {
                                    active.forEach { campaign ->
                                        viewModel.adminSimulateDelivery(campaign.id)
                                    }
                                    Toast.makeText(context, "Instantly delivered all live campaigns!", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TikTokPink, contentColor = TikTokWhite),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.FastForward, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Simulate Action Deliveries", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShopItemRow(
    coins: Int,
    price: String,
    badgeText: String,
    badgeColor: Color = TikTokGold,
    onBuyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TikTokCardDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = TikTokGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${formatNumber(coins)} Coins", color = TikTokWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(badgeText, color = badgeColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = onBuyClick,
                colors = ButtonDefaults.buttonColors(containerColor = TikTokCyan, contentColor = TikTokBlack),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(price, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Helpers
fun formatNumber(num: Int): String {
    return when {
        num >= 1000000 -> String.format("%.1fM", num / 1000000f)
        num >= 1000 -> String.format("%.1fK", num / 1000f)
        else -> num.toString()
    }
}

fun formatNumber(num: Long): String {
    return when {
        num >= 1000000000L -> String.format("%.1fB", num / 1000000000f)
        num >= 1000000L -> String.format("%.1fM", num / 1000000f)
        num >= 1000L -> String.format("%.1fK", num / 1000f)
        else -> num.toString()
    }
}
