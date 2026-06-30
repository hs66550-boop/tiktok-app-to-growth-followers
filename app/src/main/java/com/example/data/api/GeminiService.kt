package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class Part(val text: String?)

@JsonClass(generateAdapter = true)
data class Content(val parts: List<Part>)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(val contents: List<Content>)

@JsonClass(generateAdapter = true)
data class Candidate(val content: Content)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(val candidates: List<Candidate>?)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }
}

class GeminiService {
    suspend fun generateGrowthStrategy(username: String, followerCount: Int, engagementRate: Float): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w("GeminiService", "API Key is missing or placeholder. Using local strategy fallback.")
            return@withContext getLocalFallbackStrategy(username, followerCount, engagementRate)
        }

        val prompt = """
            You are a premier TikTok Growth Consultant and Viral Strategist.
            Analyze this TikTok creator's profile data and construct a 3-step actionable growth roadmap:
            - Username: $username
            - Followers: $followerCount
            - Engagement Rate: $engagementRate%
            
            Deliver a highly engaging, structured advice report. Include:
            1. An Audit Analysis of their current tier (e.g., Micro, Rising Star, Powerhouse) based on metrics.
            2. 3 actionable and hyper-relevant content pillars or video ideas for this niche (be creative, viral-focused, using current TikTok pacing).
            3. A curated list of 5 hyper-targeted viral hashtags.
            
            Format with clear headers and keep it conversational, punchy, and modern.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No suggestions found."
        } catch (e: Exception) {
            Log.e("GeminiService", "Gemini API failed: ${e.message}", e)
            getLocalFallbackStrategy(username, followerCount, engagementRate)
        }
    }

    suspend fun generateVideoScript(topic: String, style: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getLocalFallbackScript(topic, style)
        }

        val prompt = """
            Write a complete, highly engaging 30-second TikTok video script for a creator.
            - Topic: $topic
            - Style/Vibe: $style (e.g., educational, comedy, storytelling, high energy)
            
            Format the response clearly with:
            1. [HOOK] (First 3 seconds - must be scroll-stopping!)
            2. [BODY] (Engaging value points, visual direction cues in brackets like [Show close up of phone])
            3. [CTA] (Call to action to follow or comment)
            4. Suggested audio track vibe and 5 viral hashtags.
            
            Keep the script short, fast-paced, and highly optimized for retention.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Failed to generate script."
        } catch (e: Exception) {
            Log.e("GeminiService", "Gemini API failed: ${e.message}", e)
            getLocalFallbackScript(topic, style)
        }
    }

    private fun getLocalFallbackStrategy(username: String, followerCount: Int, engagementRate: Float): String {
        val tier = if (followerCount > 1000000) "TikTok Megastar" else if (followerCount > 100000) "Macro Creator" else "Rising Star"
        return """
            🚀 **TokGrow AI Profile Audit: $username ($tier)**
            
            Here is your customized growth roadmap based on your current engagement rate of **$engagementRate%**:
            
            ### 1. Retention Check
            Your engagement rate is healthy. To boost it further, focus on **Hook-to-Pacing** ratio. Ensure the first 2.5 seconds have an overlay text that prompts a question.
            
            ### 2. Recommended Content Pillars
            - **Behind the Scenes**: Show the raw, unedited process of creating your TikTok videos. 
            - **The 'Secret Listicle' Format**: Create a "Top 3 Tools/Tips I Wish I Knew Sooner" video. It is the highest-saving format on TikTok right now.
            - **Reply with Video**: Find a comment in your recent videos and answer it as a video. This prompts active community loops.
            
            ### 3. Trending Hashtags to Deploy
            #fyp #creatorsofsupport #viralinsight #contentstrategy #tiktokgrowth
            
            *(Configure your Gemini API key in the Secrets Panel to unlock live, customized AI audits!)*
        """.trimIndent()
    }

    private fun getLocalFallbackScript(topic: String, style: String): String {
        return """
            🎬 **Custom TikTok Script (Local Backup Engine)**
            **Topic:** "$topic" | **Vibe:** $style
            
            **[0:00 - 0:03] THE HOOK**
            🗣️ *"I bet you didn't know this one TikTok secret..."*
            🎥 [Visual: Point directly at the camera with an aggressive text bubble popping on screen: "DONT SCROLL! 🤫"]
            
            **[0:03 - 0:25] THE VALUE BODY**
            🗣️ *"Most creators fail because they focus on likes, but TikTok's actual goldmine is shares and saves. To get more of both, try this: outline your next video as a 3-part listicle, but leave the absolute best tip in the comments. This forces active discussion and triggers the algorithm."*
            🎥 [Visual: Screen recording showing your TikTok Analytics dashboard with a green arrow pointing to 'Saves']
            
            **[0:25 - 0:30] THE CALL TO ACTION**
            🗣️ *"Drop your TikTok handle below, and I'll audit your page next. Double tap for more creator growth secrets!"*
            🎥 [Visual: Wink and point down to comment section]
            
            **🎵 Recommended Audio:** "Lofi Synth Chill Beats" (Lowered to 15% volume under voiceover)
            **🏷️ Viral Tags:** #creatorsecrets #growthtips #$topic #tiktokstrategy
        """.trimIndent()
    }
}
