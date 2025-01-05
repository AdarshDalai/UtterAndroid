package com.cloudsbay.utterandroid.profile.data.api

import com.cloudsbay.utterandroid.auth.data.api.TokenDataStore
import com.cloudsbay.utterandroid.network.KtorClient
import com.cloudsbay.utterandroid.post.domain.model.PostsResponse
import com.cloudsbay.utterandroid.profile.domain.model.ProfileResponse
import com.cloudsbay.utterandroid.profile.domain.model.UserFollowerResponse
import com.cloudsbay.utterandroid.profile.domain.model.UserFollowingResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import javax.inject.Inject

class ProfileService @Inject constructor(
    private val ktorClient: KtorClient,
    private val tokenDataStore: TokenDataStore
){
    val client = ktorClient.getClientInstance()

    suspend fun getUserProfile(userId: String): ProfileResponse {
        val response = client.get{
            url("users/profile")
            tokenDataStore.getAccessToken()?.let {
                headers.append("Authorization", "Bearer $it")
            }
            setBody(userId)
        }
        return response.body()
    }

    suspend fun getUserFollowers(userId: String): UserFollowerResponse {
        val response = client.get {
            url("users/$userId/followers")
            tokenDataStore.getAccessToken()?.let {
                headers.append("Authorization", "Bearer $it")
            }
        }
        return response.body()
    }

    suspend fun getUserFollowing(userId: String): UserFollowingResponse {
        val response = client.get {
            url("users/$userId/following")
            tokenDataStore.getAccessToken()?.let {
                headers.append("Authorization", "Bearer $it")
            }
        }
        return response.body()
    }

    suspend fun getUserPosts(userId: String): PostsResponse {
        val response = client.get {
            url("users/$userId/posts")
            tokenDataStore.getAccessToken()?.let {
                headers.append("Authorization", "Bearer $it")
            }
        }
        return response.body()
    }

    suspend fun getUserLikes(userId: String): List<String> {
        val response = client.get {
            url("users/$userId/likes")
            tokenDataStore.getAccessToken()?.let {
                headers.append("Authorization", "Bearer $it")
            }
        }
        return response.body()
    }
}