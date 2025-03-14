package com.cloudsbay.utterandroid.feed.domain

import com.cloudsbay.utterandroid.feed.data.repository.FeedRepository
import com.cloudsbay.utterandroid.post.domain.model.PostsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FeedUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    operator fun invoke(): Flow<List<PostsResponse.Post>> = flow {
        val feedResponse = feedRepository.getFeed()
        emit(feedResponse)
    }.catch {
        throw it
    }.flowOn(Dispatchers.IO)
}