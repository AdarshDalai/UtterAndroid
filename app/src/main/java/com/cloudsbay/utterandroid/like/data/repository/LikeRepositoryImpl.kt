package com.cloudsbay.utterandroid.like.data.repository

import com.cloudsbay.utterandroid.like.data.LikeService
import com.cloudsbay.utterandroid.like.domain.LikeResponse
import javax.inject.Inject

class LikeRepositoryImpl @Inject constructor(
    private val likeService: LikeService
): LikeRepository {
    override suspend fun insertLike(postId: Int): LikeResponse {
        return likeService.insertLike(postId)
    }
}