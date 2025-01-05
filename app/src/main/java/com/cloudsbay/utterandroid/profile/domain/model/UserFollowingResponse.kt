package com.cloudsbay.utterandroid.profile.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserFollowingResponse(
    @SerialName("message") val message: String,
    @SerialName("following") val following: List<Following>

) {
    @Serializable
    data class Following(
        @SerialName("following_id") val followingId: String
    )
}