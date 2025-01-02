package com.cloudsbay.utterandroid.auth.domain.usecase

import com.cloudsbay.utterandroid.auth.data.repository.AuthRepository
import com.cloudsbay.utterandroid.auth.domain.model.LoginResponse
import com.cloudsbay.utterandroid.auth.domain.model.SignupRequest
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: SignupRequest): LoginResponse {
        return authRepository.signup(request)
    }
}