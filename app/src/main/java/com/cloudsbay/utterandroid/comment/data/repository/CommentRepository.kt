package com.cloudsbay.utterandroid.comment.data.repository

import com.cloudsbay.utterandroid.comment.domain.model.CommentResponse

interface CommentRepository {
    suspend fun createComment(postId: Int, comment: String, parentId: Int? = null): CommentResponse
}