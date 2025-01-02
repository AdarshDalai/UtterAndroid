package com.cloudsbay.utterandroid.auth.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("message") val message: String,
    @SerialName("session") val session: SessionData
) {
    @Serializable
    data class SessionData(
        @SerialName("user") val user: User,
        @SerialName("session") val tokenData: TokenData
    ) {
        @Serializable
        data class User(
            @SerialName("id") val id: String,
            @SerialName("user_metadata") val userMetadata: UserMetadata
        ) {
            @Serializable
            data class UserMetadata(
                @SerialName("bio") val bio: String,
                @SerialName("email") val email:String,
                @SerialName("email_verified") val emailVerified: Boolean,
                @SerialName("name") val name: String,
                @SerialName("profile_picture_url") val profilePictureUrl: String,
                @SerialName("sub") val sub: String,
                @SerialName("username") val username: String
            )
        }
        @Serializable
        data class TokenData(
            @SerialName("access_token")val accessToken: String,
            @SerialName("refresh_token")val refreshToken: String,
            @SerialName("expires_in")val expiresIn: String
        )
    }
}
