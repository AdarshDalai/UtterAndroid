package com.cloudsbay.utterandroid.like.domain.usecase

import com.cloudsbay.utterandroid.like.data.repository.LikeRepository
import com.cloudsbay.utterandroid.like.domain.model.LikeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LikeUseCase @Inject constructor(
    private val likeRepository: LikeRepository
) {
    operator fun invoke(postId: Int): Flow<LikeResponse> = flow {
        val likeResponse = likeRepository.insertLike(postId)
        emit(likeResponse)
    }.catch {
        throw it
    }.flowOn(Dispatchers.IO)
}