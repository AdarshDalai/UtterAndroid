package com.cloudsbay.utterandroid.comment.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentRequest(
    @SerialName("post_id")val postId: Int,
    @SerialName("comment")val comment: String,
    @SerialName("parent_id")val parentId: Int? = null
)