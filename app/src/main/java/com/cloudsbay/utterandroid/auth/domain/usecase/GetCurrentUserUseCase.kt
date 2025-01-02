package com.cloudsbay.utterandroid.auth.domain.usecase

import android.util.Log
import com.cloudsbay.utterandroid.auth.data.repository.AuthRepository
import com.cloudsbay.utterandroid.auth.domain.model.CurrentUserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val user: AuthRepository
) {
    operator fun invoke(): Flow<CurrentUserResponse> = flow {
        emit(user.getCurrentUser())
    }.catch {
        Log.e("GetCurrentUserUseCase", it.message.toString())

    }.flowOn(Dispatchers.IO)
}