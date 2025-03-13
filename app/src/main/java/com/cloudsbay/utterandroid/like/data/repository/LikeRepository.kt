package com.cloudsbay.utterandroid.like.data.repository

import com.cloudsbay.utterandroid.like.domain.model.LikeResponse

interface LikeRepository {
    suspend fun insertLike(postId: Int): LikeResponse
    suspend fun unlikePost(postId: Int): String
    suspend fun getLike(postId: Int): List<LikeResponse>
}