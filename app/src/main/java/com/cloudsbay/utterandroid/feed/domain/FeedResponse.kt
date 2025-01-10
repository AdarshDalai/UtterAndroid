package com.cloudsbay.utterandroid.feed.domain

import com.cloudsbay.utterandroid.post.domain.model.PostsResponse
import kotlinx.serialization.Serializable

@Serializable
data class FeedResponse(
    val posts: List<PostsResponse.Post>,
) {
    fun isEmpty(): Boolean {
        return posts.isEmpty()
    }
}