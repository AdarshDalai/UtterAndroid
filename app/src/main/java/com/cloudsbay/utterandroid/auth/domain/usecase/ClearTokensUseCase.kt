package com.cloudsbay.utterandroid.auth.domain.usecase

import android.util.Log
import com.cloudsbay.utterandroid.auth.data.api.TokenDataStore
import com.cloudsbay.utterandroid.auth.data.repository.TokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ClearTokensUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(): Flow<Unit> = flow {
        emit(tokenRepository.clearTokens())
    }.catch {
        Log.e("ClearTokensUseCase", "Error clearing tokens", it)

    }.flowOn(Dispatchers.IO)
}