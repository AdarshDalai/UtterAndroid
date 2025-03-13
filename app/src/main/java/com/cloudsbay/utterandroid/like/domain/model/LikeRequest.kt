package com.cloudsbay.utterandroid.like.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LikeRequest(
    @SerialName("post_id") val postId: Int
)