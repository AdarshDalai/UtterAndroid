package com.cloudsbay.utterandroid.auth.domain.usecase

import com.cloudsbay.utterandroid.auth.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SaveCurrentUserIdUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(userId: String): Flow<Unit> = flow {
        emit(authRepository.saveCurrentUserId(userId))
    }.catch {
        throw Exception("Error saving current user id")
    }.flowOn(Dispatchers.IO)
}