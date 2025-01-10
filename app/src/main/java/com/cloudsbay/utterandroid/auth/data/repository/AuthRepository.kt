package com.cloudsbay.utterandroid.auth.data.repository

import com.cloudsbay.utterandroid.auth.domain.model.CurrentUserResponse
import com.cloudsbay.utterandroid.auth.domain.model.LoginRequest
import com.cloudsbay.utterandroid.auth.domain.model.LoginResponse
import com.cloudsbay.utterandroid.auth.domain.model.SignupRequest

interface AuthRepository {
    suspend fun signup(request: SignupRequest): LoginResponse
    suspend fun login(request: LoginRequest): LoginResponse
    suspend fun logout(): String
    suspend fun getCurrentUser(): CurrentUserResponse
    suspend fun saveCurrentUserId(userId: String)
    suspend fun getCurrentUserId(): String
}