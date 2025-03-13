package com.cloudsbay.utterandroid.post.data.repository

import android.net.Uri
import com.cloudsbay.utterandroid.post.domain.model.PostsResponse
import java.io.File

interface PostRepository {
    suspend fun uploadPost(content: String, mediaFile: Uri): PostsResponse.Post
}