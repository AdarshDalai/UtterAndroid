package com.cloudsbay.utterandroid.feed.data.repository

import com.cloudsbay.utterandroid.feed.data.FeedService
import com.cloudsbay.utterandroid.feed.domain.FeedResponse
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    private val feedService: FeedService
): FeedRepository {
    override suspend fun getFeed(): FeedResponse {
        return feedService.getFeed()
    }
}