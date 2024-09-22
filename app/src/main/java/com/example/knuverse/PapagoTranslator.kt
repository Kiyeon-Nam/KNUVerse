package com.example.knuverse

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType

private val db = FirebaseFirestore.getInstance()

class PapagoTranslator {
    private val client = OkHttpClient()
    private val gson = Gson()

    private val clientId = "ztrwq4rsa5"
    private val clientSecret = "qxmoMkFgIq0Gbc789rziY1PNCpDDNmtIL8c4XpPw"

    // 번역 요청
    suspend fun translateText(source: String, target: String, text: String): String? {
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaType(),
            gson.toJson(PapagoRequest(source, target, text))
        )

        val request = Request.Builder()
            .url("https://naveropenapi.apigw.ntruss.com/nmt/v1/translation")
            .post(requestBody)
            .addHeader("X-NCP-APIGW-API-KEY-ID", clientId)
            .addHeader("X-NCP-APIGW-API-KEY", clientSecret)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val papagoResponse = gson.fromJson(responseBody, PapagoResponse::class.java)
                    papagoResponse.message.result.translatedText
                } else {
                    Log.e("Papago API", "Error: ${response.code} ${response.message}")
                    null
                }
            } catch (e: Exception) {
                Log.e("Papago API", "Exception: ${e.message}")
                null
            }
        }
    }
}