package com.cloudsbay.utterandroid.post.data.api

import com.cloudsbay.utterandroid.auth.data.api.TokenDataStore
import com.cloudsbay.utterandroid.network.KtorClient
import com.cloudsbay.utterandroid.post.domain.model.PostsResponse
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.content.PartData
import io.ktor.http.content.streamProvider
import io.ktor.http.parameters
import java.io.File
import javax.inject.Inject

class PostService @Inject constructor(
    private val ktorClient: KtorClient,
    private val tokenDataStore: TokenDataStore
) {
    private val client = ktorClient.getClientInstance()

    suspend fun uploadPost(content: String, mediaFile: File): PostsResponse.Post {
        val response = client.post {
            url("posts/post")
            tokenDataStore.getAccessToken()?.let {
                headers.append("Authorization", "Bearer $it")
            }
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("content", content)
                        append("media", mediaFile.readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, getMimeType(mediaFile))
                            append(HttpHeaders.ContentDisposition, "filename=${mediaFile.name}")
                        })
                    }
                )
            )
        }
        return response.body()
    }

    private fun getMimeType(file: File): String {
        return when (file.extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "mp4" -> "video/mp4"
            "mov" -> "video/quicktime"
            else -> "application/octet-stream"
        }
    }
}