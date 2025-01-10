package com.cloudsbay.utterandroid.profile.domain.usecase

import com.cloudsbay.utterandroid.auth.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetCurrentUserIdUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<String> = flow {
        emit(authRepository.getCurrentUserId())
    }.catch {
        throw it
    }.flowOn(Dispatchers.IO)
}