package com.cloudsbay.utterandroid.post.data.repository

import com.cloudsbay.utterandroid.post.domain.model.PostsResponse
import java.io.File

interface PostRepository {
    suspend fun uploadPost(content: String, mediaFile: File): PostsResponse.Post
}