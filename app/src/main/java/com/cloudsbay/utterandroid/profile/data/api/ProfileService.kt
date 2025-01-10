package com.cloudsbay.utterandroid.profile.data.api

import com.cloudsbay.utterandroid.auth.data.api.TokenDataStore
import com.cloudsbay.utterandroid.network.KtorClient
import com.cloudsbay.utterandroid.post.domain.model.PostsResponse
import com.cloudsbay.utterandroid.profile.domain.model.ProfileResponse
import com.cloudsbay.utterandroid.profile.domain.model.UserFollowerResponse
import com.cloudsbay.utterandroid.profile.domain.model.UserFollowingResponse
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject

class ProfileService @Inject constructor(
    private val ktorClient: KtorClient,
    private val tokenDataStore: TokenDataStore
){
    val client = ktorClient.getClientInstance()

    suspend fun getUserProfile(userId: String): ProfileResponse {
        val response = client.get{
            url("users/$userId")
            tokenDataStore.getAccessToken()?.let {
                headers.append("Authorization", "Bearer $it")
            }
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

    suspend fun updateUsername(username: String) {
        client.put {
            url("users/update_username?=$username")
            tokenDataStore.getAccessToken()?.let {
                headers.append("Authorization", "Bearer $it")
            }
            parameter("username", username)
        }
    }

    suspend fun updateBio(bio: String) {
        client.put {
            url("users/update_bio?=$bio")
            tokenDataStore.getAccessToken()?.let {
                headers.append("Authorization", "Bearer $it")
            }
            parameter("bio", bio)
        }
    }

    suspend fun updateProfilePicture(profilePicture: File) {
        client.put {
            url("users/update_profile_picture")
            tokenDataStore.getAccessToken()?.let {
                headers.append("Authorization", "Bearer $it")
            }
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("profile_picture", profilePicture.readBytes())
                    }
                )
            )
        }
    }
}