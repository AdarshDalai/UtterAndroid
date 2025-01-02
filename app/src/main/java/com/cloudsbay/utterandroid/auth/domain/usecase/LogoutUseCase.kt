package com.cloudsbay.utterandroid.auth.domain.usecase

import android.util.Log
import com.cloudsbay.utterandroid.auth.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<String> = flow {
        emit( authRepository.logout())
    }.catch {
        Log.e("LogoutUseCase", "invoke: ${it.message}")
    }.flowOn(Dispatchers.IO)
}