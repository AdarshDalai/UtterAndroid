package com.cloudsbay.utterandroid.profile.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    @SerialName("message") val message: String,
    @SerialName("user") val user: UserProfile
) {

    @Serializable
    data class UserProfile(
        @SerialName("id") val id: String,
        @SerialName("email") val email: String,
        @SerialName("username") val username: String,
        @SerialName("profile_picture_url") val profilePictureUrl: String? = null,
        @SerialName("bio") val bio: String? = null,
        @SerialName("created_at") val createdAt: String, // ISO 8601 datetime format
        @SerialName("is_private") val isPrivate: Boolean,
        @SerialName("name") val name: String
    )
}