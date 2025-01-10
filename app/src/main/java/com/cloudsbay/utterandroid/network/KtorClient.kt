package com.cloudsbay.utterandroid.network

import android.util.Log
import com.cloudsbay.utterandroid.auth.data.api.TokenDataStore
import com.cloudsbay.utterandroid.auth.domain.model.TokenRefresh
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KtorClient @Inject constructor(
    private val tokenDataStore: TokenDataStore
) {

    private val client = HttpClient(Android) {
        defaultRequest {
            url("https://utter-backend.onrender.com/")
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println(message)
                }
            }
            level = LogLevel.ALL
        }

        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }

        install(Auth) {
            bearer {
                refreshTokens {
                    Log.d("KtorClient", "refreshTokens: ${tokenDataStore.getRefreshToken()}")
                    val token = client.post {
                        url("auth/refresh_token")
                        setBody(mapOf("refresh_token" to tokenDataStore.getRefreshToken()))
                        contentType(ContentType.Application.Json)
                    }.body<TokenRefresh>()
                    tokenDataStore.saveTokens(token.accessToken, token.refreshToken)
                    Log.d("KtorClient", "refreshTokens: $token")
                    BearerTokens(token.accessToken, token.refreshToken)
                }
            }
        }

    }

    fun getClientInstance() = client
}