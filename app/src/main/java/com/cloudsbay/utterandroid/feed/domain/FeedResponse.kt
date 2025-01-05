package com.cloudsbay.utterandroid.feed.domain

import com.cloudsbay.utterandroid.post.domain.model.PostsResponse

data class FeedResponse(
    val posts: List<PostsResponse.Post>,
)