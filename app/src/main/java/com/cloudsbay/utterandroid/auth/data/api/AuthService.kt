package com.cloudsbay.utterandroid.auth.data.api

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cloudsbay.utterandroid.auth.domain.model.CurrentUserResponse
import com.cloudsbay.utterandroid.auth.domain.model.LoginRequest
import com.cloudsbay.utterandroid.auth.domain.model.LoginResponse
import com.cloudsbay.utterandroid.auth.domain.model.SignupRequest
import com.cloudsbay.utterandroid.network.KtorClient
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class AuthService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ktorClient: KtorClient,
    private val tokenDataStore: TokenDataStore
) {

    var client = ktorClient.getClientInstance()

    suspend fun getAccessToken(): String? = tokenDataStore.getAccessToken()
    suspend fun getRefreshToken(): String? = tokenDataStore.getRefreshToken()
    private val Context.dataStore by preferencesDataStore(name = "CurrentUserId")
    private val currentUserId = stringPreferencesKey("user_id")

    fun saveCurrentUserId(userId: String) {
        runBlocking {
            context.dataStore.edit { preferences ->
                preferences[currentUserId] = userId
            }
        }
    }

    fun getCurrentUserId(): String? {
        return runBlocking {
            context.dataStore.data.firstOrNull()?.get(currentUserId)
        }
    }

    fun clearCurrentUserId() {
        runBlocking {
            context.dataStore.edit { preferences ->
                preferences.remove(currentUserId)
            }
        }
    }

    suspend fun signup(request: SignupRequest): LoginResponse {
        return client.post("auth/signup"){
            contentType(ContentType.Application.Json)
            setBody(
                SignupRequest(
                    email = request.email,
                    password = request.password,
                    username = request.username,
                    name = request.name,
                    bio = request.bio,
                    profilePictureUrl = request.profilePictureUrl
                )
            )
        }.body()
    }

    suspend fun login(request: LoginRequest): LoginResponse {
        return client.post("auth/login"){
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(request.email, request.password))
        }.body()
    }

    suspend fun logout(): String {
        val accessToken = getAccessToken()
        Log.d("AuthService","Bearer ${accessToken}")
        return client.post {
            url("auth/logout")
            headers {
                append(HttpHeaders.Authorization, "Bearer ${accessToken}")
            }
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun getCurrentUser(): CurrentUserResponse {
        val accessToken = getAccessToken()
        Log.d("AuthService","Bearer ${accessToken}")
        return client.get{
            url("auth/current_user")
            headers {
                append(HttpHeaders.Authorization, "Bearer ${accessToken}")
            }
            contentType(ContentType.Application.Json)
        }.body()
    }
}