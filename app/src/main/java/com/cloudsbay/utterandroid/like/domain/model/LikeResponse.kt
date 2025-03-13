package com.cloudsbay.utterandroid.like.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LikeResponse(
    @SerialName("id") val id: Int,
    @SerialName("post_id") val postId: Int,
    @SerialName("user_id") val userId: String
)