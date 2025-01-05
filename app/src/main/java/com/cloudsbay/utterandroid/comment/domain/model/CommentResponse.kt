package com.cloudsbay.utterandroid.comment.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    @SerialName("id")val id: Int,
    @SerialName("post_id")val postId: Int,
    @SerialName("user_id")val userId: String,
    @SerialName("comment")val comment: String,
    @SerialName("parent_id")val parentId: Int? = null,
    @SerialName("created_at")val createdAt: String
)