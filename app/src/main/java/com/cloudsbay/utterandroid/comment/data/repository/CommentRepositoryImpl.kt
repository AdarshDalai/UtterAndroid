package com.cloudsbay.utterandroid.comment.data.repository

import com.cloudsbay.utterandroid.comment.data.CommentService
import com.cloudsbay.utterandroid.comment.domain.model.CommentResponse
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentService: CommentService
): CommentRepository {
    override suspend fun createComment(
        postId: Int,
        comment: String,
        parentId: Int?
    ): CommentResponse {
        return commentService.createComment(postId, comment, parentId)
    }
}