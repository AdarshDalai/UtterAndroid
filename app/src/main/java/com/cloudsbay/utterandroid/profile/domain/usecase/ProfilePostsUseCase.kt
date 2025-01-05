package com.cloudsbay.utterandroid.profile.domain.usecase

import android.util.Log
import com.cloudsbay.utterandroid.post.domain.model.PostsResponse
import com.cloudsbay.utterandroid.profile.data.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProfilePostsUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(userId: String): Flow<PostsResponse> = flow {
        emit(profileRepository.getUserPosts(userId))
    }.catch {
        Log.e("ProfilePostsUseCase", "invoke: ${it.message}")
    }.flowOn(Dispatchers.IO)
}