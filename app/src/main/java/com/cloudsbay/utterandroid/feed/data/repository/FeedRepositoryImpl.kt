package com.cloudsbay.utterandroid.feed.data.repository

import com.cloudsbay.utterandroid.feed.data.FeedService
import com.cloudsbay.utterandroid.post.domain.model.PostsResponse
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    private val feedService: FeedService
): FeedRepository {
    override suspend fun getFeed(): List<PostsResponse.Post> {
        return feedService.getFeed()
    }
}