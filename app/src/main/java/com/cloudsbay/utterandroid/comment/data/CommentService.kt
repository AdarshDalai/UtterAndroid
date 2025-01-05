package com.cloudsbay.utterandroid.comment.data

import com.cloudsbay.utterandroid.auth.data.api.TokenDataStore
import com.cloudsbay.utterandroid.comment.domain.model.CommentRequest
import com.cloudsbay.utterandroid.comment.domain.model.CommentResponse
import com.cloudsbay.utterandroid.network.KtorClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import javax.inject.Inject

class CommentService @Inject constructor(
    private val ktorClient: KtorClient,
    private val tokenDataStore: TokenDataStore
) {
    var client = ktorClient.getClientInstance()
    suspend fun createComment(postId: Int, comment: String, parentId: Int? = null): CommentResponse {
        return client.post {
            url("comments/create_comment")
            tokenDataStore.getAccessToken()?.let {
                headers {
                    append("Authorization", "Bearer $it")
                }
                setBody(CommentRequest(postId, comment, parentId))
            }
        }.body()
    }
}