package com.cloudsbay.utterandroid.like.data.repository

import com.cloudsbay.utterandroid.like.data.api.LikeService
import com.cloudsbay.utterandroid.like.domain.model.LikeResponse
import javax.inject.Inject

class LikeRepositoryImpl @Inject constructor(
    private val likeService: LikeService
): LikeRepository {
    override suspend fun insertLike(postId: Int): LikeResponse {
        return likeService.insertLike(postId)
    }

    override suspend fun unlikePost(postId: Int): String {
        return likeService.unlikePost(postId)
    }

    override suspend fun getLike(postId: Int): List<LikeResponse> {
        return likeService.getLike(postId)
    }
}