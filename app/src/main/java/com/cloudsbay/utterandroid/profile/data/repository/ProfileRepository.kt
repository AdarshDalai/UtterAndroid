package com.cloudsbay.utterandroid.profile.data.repository

import com.cloudsbay.utterandroid.post.domain.model.PostsResponse
import com.cloudsbay.utterandroid.profile.domain.model.ProfileResponse
import com.cloudsbay.utterandroid.profile.domain.model.UserFollowerResponse
import com.cloudsbay.utterandroid.profile.domain.model.UserFollowingResponse

interface ProfileRepository {
    suspend fun getUserProfile(userId: String): ProfileResponse
    suspend fun getUserFollowers(userId: String): UserFollowerResponse
    suspend fun getUserFollowing(userId: String): UserFollowingResponse
    suspend fun getUserPosts(userId: String): PostsResponse
    suspend fun getUserLikes(userId: String): List<String>
}