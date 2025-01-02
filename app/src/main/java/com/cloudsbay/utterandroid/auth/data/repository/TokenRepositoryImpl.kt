package com.cloudsbay.utterandroid.auth.data.repository

import com.cloudsbay.utterandroid.auth.data.api.TokenDataStore
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val tokenDataStore: TokenDataStore
):TokenRepository {
    override suspend fun getAccessToken(): String? {
        return tokenDataStore.getAccessToken()
    }

    override suspend fun getRefreshToken(): String? {
        return tokenDataStore.getRefreshToken()
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        tokenDataStore.saveTokens(accessToken, refreshToken)
    }

    override suspend fun clearTokens() {
        tokenDataStore.clearTokens()
    }

}