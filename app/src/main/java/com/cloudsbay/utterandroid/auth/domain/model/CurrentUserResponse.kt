package com.cloudsbay.utterandroid.auth.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentUserResponse(
    @SerialName("user") val user: CurrentUser
) {
    @Serializable
    data class CurrentUser(
        @SerialName("sub") val sub: String,
        @SerialName("user_metadata")val userMetadata: LoginResponse.SessionData.User.UserMetadata
    )
}