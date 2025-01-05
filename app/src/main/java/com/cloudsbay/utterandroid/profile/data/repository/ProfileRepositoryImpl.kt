package com.cloudsbay.utterandroid.profile.data.repository

import com.cloudsbay.utterandroid.post.domain.model.PostsResponse
import com.cloudsbay.utterandroid.profile.data.api.ProfileDataStore
import com.cloudsbay.utterandroid.profile.data.api.ProfileService
import com.cloudsbay.utterandroid.profile.domain.model.ProfileResponse
import com.cloudsbay.utterandroid.profile.domain.model.UserFollowerResponse
import com.cloudsbay.utterandroid.profile.domain.model.UserFollowingResponse
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileService: ProfileService,
    private val profileDataStore: ProfileDataStore
): ProfileRepository {
    override suspend fun getUserProfile(userId: String): ProfileResponse {
        val response = profileService.getUserProfile(userId)
        return response
    }

    override suspend fun getUserFollowers(userId: String): UserFollowerResponse {
        val response = profileService.getUserFollowers(userId)
        return response
    }

    override suspend fun getUserFollowing(userId: String): UserFollowingResponse {
        val response = profileService.getUserFollowing(userId)
        return response
    }

    override suspend fun getUserPosts(userId: String): PostsResponse {
        val response = profileService.getUserPosts(userId)
        return response
    }

    override suspend fun getUserLikes(userId: String): List<String> {
        val response = profileService.getUserLikes(userId)
        return response

    }

}