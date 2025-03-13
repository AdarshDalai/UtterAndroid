package com.cloudsbay.utterandroid.post.data.api

import android.net.Uri
import android.util.Log
import com.cloudsbay.utterandroid.auth.data.api.TokenDataStore
import com.cloudsbay.utterandroid.network.KtorClient
import com.cloudsbay.utterandroid.post.data.FileReader
import com.cloudsbay.utterandroid.post.domain.model.PostsResponse
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import javax.inject.Inject

class PostService @Inject constructor(
    private val ktorClient: KtorClient,
    private val tokenDataStore: TokenDataStore,
    private val fileReader: FileReader
) {
    private val client = ktorClient.getClientInstance()

    suspend fun uploadPost(caption: String, mediaFile: Uri): PostsResponse.Post {
        val info = fileReader.uriToFileInfo(mediaFile)
        val accessToken = tokenDataStore.getAccessToken() ?: throw IllegalStateException("Access token is missing")

        val response: HttpResponse = client.post("posts/post") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("caption", caption)
                        append("media", info.bytes, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            Log.d("PostService", "uploadPost: ${info.mimeType}")
                        })
                    }
                )
            )
            headers {
                append("Authorization", "Bearer $accessToken")
                append("Accept", "application/json")
            }
        }

        if (response.status.value in 200..299) {
            return response.body()
        } else {
            throw IllegalStateException("Failed to upload post: ${response.status}")
        }
    }
}