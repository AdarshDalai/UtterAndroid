package com.cloudsbay.utterandroid.like.data.api

import com.cloudsbay.utterandroid.auth.data.api.TokenDataStore
import com.cloudsbay.utterandroid.like.domain.model.LikeRequest
import com.cloudsbay.utterandroid.like.domain.model.LikeResponse
import com.cloudsbay.utterandroid.network.KtorClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import javax.inject.Inject

class LikeService @Inject constructor(
    private val ktorClient: KtorClient,
    private val tokenDataStore: TokenDataStore
) {
    val client = ktorClient.getClientInstance()
    suspend fun insertLike(postId: Int): LikeResponse {
        return client.post {
            url("likes/like_post")
            tokenDataStore.getAccessToken()?.let {
                headers {
                    append("Authorization", "Bearer $it")
                    setBody(LikeRequest(postId))
                }
            }
        }.body()
    }

    suspend fun unlikePost(postId: Int): String {
        return client.delete {
            url("likes/unlike_post")
            tokenDataStore.getAccessToken()?.let {
                headers {
                    append("Authorization", "Bearer $it")
                }
            }
        }.body()
    }

    suspend fun getLike(postId: Int): List<LikeResponse> {
        return client.get {
            url("likes/$postId")
            tokenDataStore.getAccessToken()?.let {
                headers {
                    append("Authorization", "Bearer $it")
                }
            }
            parameter("post_id", postId)
        }.body()
    }
}