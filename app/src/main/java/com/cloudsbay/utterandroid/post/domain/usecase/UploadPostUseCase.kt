package com.cloudsbay.utterandroid.post.domain.usecase

import android.net.Uri
import android.util.Log
import com.cloudsbay.utterandroid.post.data.repository.PostRepository
import com.cloudsbay.utterandroid.post.domain.model.PostsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class UploadPostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    operator fun invoke(content: String, mediaFile: Uri): Flow<PostsResponse.Post> = flow {
        emit(postRepository.uploadPost(content, mediaFile))
    }.catch {
        Log.e("UploadPostUseCase", "invoke: ${it.message}")
    }.flowOn(Dispatchers.IO)
}