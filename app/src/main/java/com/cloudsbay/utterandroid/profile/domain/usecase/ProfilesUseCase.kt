package com.cloudsbay.utterandroid.profile.domain.usecase

import android.util.Log
import com.cloudsbay.utterandroid.profile.data.repository.ProfileRepository
import com.cloudsbay.utterandroid.profile.domain.model.ProfileResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProfilesUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(userId: String): Flow<ProfileResponse> = flow {
        emit(profileRepository.getUserProfile(userId))
    }.catch {
        Log.e("ProfilesUseCase", "invoke: ${it.message}")
    }.flowOn(Dispatchers.IO)
}