package com.cloudsbay.utterandroid.auth.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SignupRequest(
    val email: String,
    val password: String,
    val username: String,
    val name: String,
    val bio: String,
    @SerialName("profile_picture_url") val profilePictureUrl: String? = null
)