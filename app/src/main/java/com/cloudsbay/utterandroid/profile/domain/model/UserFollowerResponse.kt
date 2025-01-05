package com.cloudsbay.utterandroid.profile.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserFollowerResponse(
    @SerialName("message")val message: String,
    @SerialName("followers")val followers: List<Follower>
) {
    @Serializable
    data class Follower(
        @SerialName("follower_id")val followerId: String
    )
}