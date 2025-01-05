package com.cloudsbay.utterandroid.post.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostsResponse(
    @SerialName("message") val message: String,
    @SerialName("posts") val posts: List<Post>
) {
    @Serializable
    data class Post(
        @SerialName("id") val id: Int,
        @SerialName("user_id") val userId: String,
        @SerialName("content") val content: String,
        @SerialName("media_url") val mediaUrl: String,
        @SerialName("created_at") val createdAt: String
    )
}