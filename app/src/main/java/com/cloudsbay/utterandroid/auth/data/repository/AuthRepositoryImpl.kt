package com.cloudsbay.utterandroid.auth.data.repository

import com.cloudsbay.utterandroid.auth.data.api.TokenDataStore
import com.cloudsbay.utterandroid.auth.data.api.AuthService
import com.cloudsbay.utterandroid.auth.domain.model.CurrentUserResponse
import com.cloudsbay.utterandroid.auth.domain.model.LoginRequest
import com.cloudsbay.utterandroid.auth.domain.model.LoginResponse
import com.cloudsbay.utterandroid.auth.domain.model.SignupRequest
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val tokenDataStore: TokenDataStore
): AuthRepository {
    override suspend fun signup(request: SignupRequest): LoginResponse {
        val response = authService.signup(request)
        tokenDataStore.saveTokens(
            accessToken = response.session.tokenData.accessToken,
            refreshToken = response.session.tokenData.refreshToken
        )
        authService.saveCurrentUserId(
            userId = response.session.user.id
        )
        return response
    }

    override suspend fun login(request: LoginRequest): LoginResponse {
        val response =  authService.login(request)
        tokenDataStore.saveTokens(
            accessToken = response.session.tokenData.accessToken,
            refreshToken = response.session.tokenData.refreshToken
        )
        return response
    }

    override suspend fun logout(): String {
        val response = authService.logout()
        tokenDataStore.clearTokens()
        return response
    }

    override suspend fun getCurrentUser(): CurrentUserResponse {
        return authService.getCurrentUser()
    }

}