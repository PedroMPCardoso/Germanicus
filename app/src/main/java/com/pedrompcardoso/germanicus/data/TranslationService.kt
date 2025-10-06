package com.pedrompcardoso.germanicus.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

/**
 * Service for translating words using the MyMemory API
 * Free tier: 1,000 requests per day
 */
class TranslationService {
    
    private val client = OkHttpClient()
    private val baseUrl = "https://api.mymemory.translated.net/get"
    
    /**
     * Translate a German word to English
     * @param germanWord The German word to translate
     * @return The English translation or null if translation fails
     */
    suspend fun translateGermanToEnglish(germanWord: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val encodedWord = URLEncoder.encode(germanWord, "UTF-8")
                val url = "$baseUrl?q=$encodedWord&langpair=de|en"
                
                Log.d("TranslationService", "Translating: $germanWord")
                Log.d("TranslationService", "URL: $url")
                
                val request = Request.Builder()
                    .url(url)
                    .build()
                
                val response = client.newCall(request).execute()
                
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("TranslationService", "Response: $responseBody")
                    
                    responseBody?.let { body ->
                        val jsonResponse = JSONObject(body)
                        val responseStatus = jsonResponse.getInt("responseStatus")
                        
                        if (responseStatus == 200) {
                            val responseData = jsonResponse.getJSONObject("responseData")
                            val translatedText = responseData.getString("translatedText")
                            
                            // Clean up the translation (remove extra spaces, etc.)
                            val cleanTranslation = translatedText.trim()
                            
                            Log.d("TranslationService", "Translation successful: $cleanTranslation")
                            return@withContext cleanTranslation
                        } else {
                            Log.e("TranslationService", "API returned error status: $responseStatus")
                        }
                    }
                } else {
                    Log.e("TranslationService", "HTTP error: ${response.code}")
                }
                
                null
            } catch (e: Exception) {
                Log.e("TranslationService", "Translation failed", e)
                null
            }
        }
    }
    
    /**
     * Check if the service is available (basic connectivity check)
     */
    suspend fun isAvailable(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val testRequest = Request.Builder()
                    .url("https://api.mymemory.translated.net/get?q=test&langpair=en|de")
                    .build()
                
                val response = client.newCall(testRequest).execute()
                response.isSuccessful
            } catch (e: Exception) {
                false
            }
        }
    }
}
