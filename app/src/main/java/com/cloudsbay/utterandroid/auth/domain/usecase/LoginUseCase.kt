package com.cloudsbay.utterandroid.auth.domain.usecase

import android.util.Log
import com.cloudsbay.utterandroid.auth.data.repository.AuthRepository
import com.cloudsbay.utterandroid.auth.domain.model.LoginRequest
import com.cloudsbay.utterandroid.auth.domain.model.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(request: LoginRequest): Flow<LoginResponse> = flow {
         emit(authRepository.login(request))
    }.catch {
        Log.e("LoginUseCase", "invoke: ${it.message}")
    }.flowOn(Dispatchers.IO)
}