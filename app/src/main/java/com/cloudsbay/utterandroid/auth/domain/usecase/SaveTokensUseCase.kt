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

class SaveTokensUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(accessToken: String, refreshToken: String): Flow<Unit> = flow {
        emit(tokenRepository.saveTokens(accessToken, refreshToken))
    }.catch {
        Log.e("SaveTokensUseCase", it.message ?: "Error saving tokens")
    }.flowOn(Dispatchers.IO)
}