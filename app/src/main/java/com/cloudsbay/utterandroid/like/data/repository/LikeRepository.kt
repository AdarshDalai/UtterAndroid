package com.cloudsbay.utterandroid.like.data.repository

import com.cloudsbay.utterandroid.like.domain.LikeResponse

interface LikeRepository {
    suspend fun insertLike(postId: Int): LikeResponse
}