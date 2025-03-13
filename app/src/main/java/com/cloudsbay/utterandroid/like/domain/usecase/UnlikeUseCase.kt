package com.cloudsbay.utterandroid.like.domain.usecase

import com.cloudsbay.utterandroid.like.data.repository.LikeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UnlikeUseCase @Inject constructor(
    private val likeRepository: LikeRepository
){
    operator fun invoke(postId: Int): Flow<String> = flow {
        emit(likeRepository.unlikePost(postId))
    }.catch {
        throw it
    }.flowOn(Dispatchers.IO)
}