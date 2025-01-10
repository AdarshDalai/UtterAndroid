package com.cloudsbay.utterandroid.feed.data.repository

import com.cloudsbay.utterandroid.post.domain.model.PostsResponse

interface FeedRepository {
    suspend fun getFeed(): List<PostsResponse.Post>

}