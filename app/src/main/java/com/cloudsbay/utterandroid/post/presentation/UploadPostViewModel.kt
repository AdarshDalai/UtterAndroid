package com.cloudsbay.utterandroid.post.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudsbay.utterandroid.post.domain.model.PostsResponse
import com.cloudsbay.utterandroid.post.domain.usecase.UploadPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadPostViewModel @Inject constructor(
    private val uploadPostUseCase: UploadPostUseCase
): ViewModel() {
    sealed class UploadPostState {
        data object Idle : UploadPostState()
        data object Loading : UploadPostState()
        data class Success(val post: PostsResponse.Post) : UploadPostState()
        data class Error(val message: String) : UploadPostState()
    }

    private val _uploadPostState = MutableStateFlow<UploadPostState>(UploadPostState.Idle)
    val uploadPostState: StateFlow<UploadPostState> = _uploadPostState

    fun uploadPost(content: String, mediaFile: File) {
        _uploadPostState.value = UploadPostState.Loading
        viewModelScope.launch {
            runCatching {
                uploadPostUseCase(content, mediaFile).collect { post ->
                    _uploadPostState.value = UploadPostState.Success(post)
                }
            }.onFailure {
                _uploadPostState.value = UploadPostState.Error(it.message ?: "An unexpected error occurred")
            }
        }
    }
}