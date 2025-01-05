package com.cloudsbay.utterandroid.feed.data.repository

import com.cloudsbay.utterandroid.feed.domain.FeedResponse

interface FeedRepository {
    suspend fun getFeed(): FeedResponse

}