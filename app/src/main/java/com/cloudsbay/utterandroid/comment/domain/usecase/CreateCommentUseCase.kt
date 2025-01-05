package com.cloudsbay.utterandroid.comment.domain.usecase

import com.cloudsbay.utterandroid.comment.data.repository.CommentRepository
import com.cloudsbay.utterandroid.comment.domain.model.CommentResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CreateCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    operator fun invoke(postId: Int, comment: String, parentId: Int? = null): Flow<CommentResponse> = flow {
        val commentResponse = commentRepository.createComment(postId, comment, parentId)
        emit(commentResponse)
    }.catch {
        throw it
    }.flowOn(Dispatchers.IO)
}