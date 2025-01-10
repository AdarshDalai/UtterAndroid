package com.cloudsbay.utterandroid.auth.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SignupRequest(
    @SerialName("email")val email: String,
    @SerialName("password")val password: String,
    @SerialName("username")val username: String,
    @SerialName("name")val name: String,
    @SerialName("bio")val bio: String,
    @SerialName("profile_picture_url") val profilePictureUrl: String? = null
)