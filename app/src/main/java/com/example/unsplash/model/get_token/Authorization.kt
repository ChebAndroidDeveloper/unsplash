package com.example.unsplash.model.get_token

import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request


class Authorization {
    private val clientId = "B-sFQHPvFwhgWheqkote68fcwnTlaeMYg1JULRk3gv4"
    private val clientSecret = "L7mz3tteWGRl0aeL13O4R5gaMrhu-qxtFku7rGp4cHI"
    private val redirectUri = "myapp://unsplash"
    private val authUrl = "https://unsplash.com/oauth/authorize"
    private val tokenUrl = "https://unsplash.com/oauth/token"

    fun getAuthUrl(): String {
        return Uri.parse(authUrl)
            .buildUpon()
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("redirect_uri", redirectUri)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("scope",
                "public read_user write_user read_photos write_photos " +
                        "write_likes write_followers read_collections write_collections")
            .build()
            .toString()
    }

    suspend fun getAccessToken(code: String): String? {
        Log.d("!!!", code)
        return withContext(Dispatchers.IO) {
            Log.d("!!!", "Executing code inside withContext")
            try {
                val formBody = FormBody.Builder()
                    .add("client_id", clientId)
                    .add("client_secret", clientSecret)
                    .add("redirect_uri", redirectUri)
                    .add("code", code)
                    .add("grant_type", "authorization_code")
                    .build()

                val request = Request.Builder()
                    .url(tokenUrl)
                    .post(formBody)
                    .build()

                Log.d("!!!", "Sending access token request")
                val response = OkHttpClient().newCall(request).execute()
                Log.d("!!!", "Received access token response")

                val responseBody = response.body?.string() ?: ""
                Log.d("!!!", "Response body: $responseBody")

                if (response.isSuccessful) {
                    val gson = Gson()
                    val json = gson.fromJson(responseBody, JsonObject::class.java)
                    val accessToken = json.get("access_token")?.asString
                    Log.d("!!!", "Access token: $accessToken")
                    accessToken
                } else {
                    Log.e("!!!", "Error response: ${response.code} - ${response.message}")
                    null
                }
            } catch (e: Exception) {
                Log.e("!!!", "Error getting access token: ${e.message}")
                null
            }
        }
    }


}



