package com.cloudsbay.utterandroid.like.data

import com.cloudsbay.utterandroid.auth.data.api.TokenDataStore
import com.cloudsbay.utterandroid.like.domain.LikeRequest
import com.cloudsbay.utterandroid.like.domain.LikeResponse
import com.cloudsbay.utterandroid.network.KtorClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
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
            url("likes/insert_like")
            tokenDataStore.getAccessToken()?.let {
                headers {
                    append("Authorization", "Bearer $it")
                    setBody(LikeRequest(postId))
                }
            }
        }.body()
    }
}