package com.cloudsbay.utterandroid.feed.data

import com.cloudsbay.utterandroid.auth.data.api.TokenDataStore
import com.cloudsbay.utterandroid.feed.domain.FeedResponse
import com.cloudsbay.utterandroid.network.KtorClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.url
import javax.inject.Inject

class FeedService @Inject constructor(
    private val ktorClient: KtorClient,
    private val tokenDataStore: TokenDataStore
) {
    val client = ktorClient.getClientInstance()
    suspend fun getFeed(): FeedResponse {
        return client.get {
            url("feed")
            tokenDataStore.getAccessToken()?.let {
                headers {
                    append("Authorization", "Bearer $it")
                }
            }
        }.body()
    }
}