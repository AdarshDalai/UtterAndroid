package com.cloudsbay.utterandroid.post.data.repository

import android.net.Uri
import com.cloudsbay.utterandroid.post.data.api.PostDataStore
import com.cloudsbay.utterandroid.post.data.api.PostService
import com.cloudsbay.utterandroid.post.domain.model.PostsResponse
import java.io.File
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postService: PostService,
    private val postDataStore: PostDataStore
): PostRepository {
    override suspend fun uploadPost(content: String, mediaFile: Uri): PostsResponse.Post {
        val response =  postService.uploadPost(content, mediaFile)
        return response
    }

}