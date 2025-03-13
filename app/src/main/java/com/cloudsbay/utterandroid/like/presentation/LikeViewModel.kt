package com.cloudsbay.utterandroid.like.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudsbay.utterandroid.like.domain.model.LikeResponse
import com.cloudsbay.utterandroid.like.domain.usecase.GetLikeUseCase
import com.cloudsbay.utterandroid.like.domain.usecase.LikeUseCase
import com.cloudsbay.utterandroid.like.domain.usecase.UnlikeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(
    private val likeUseCase: LikeUseCase,
    private val unlikeUseCase: UnlikeUseCase,
    private val getLikeUseCase: GetLikeUseCase
) : ViewModel() {

    sealed class LikeState {
        object Idle : LikeState()
        object Loading : LikeState()
        data class Success(val isLiked: Flow<LikeResponse>) : LikeState()
        data class Error(val errorMessage: String) : LikeState()
    }

    private val _likeState = MutableStateFlow<LikeState>(LikeState.Idle)
    val likeState: StateFlow<LikeState> = _likeState

    fun likePost(postId: Int) {
        viewModelScope.launch {
            _likeState.value = LikeState.Loading
            try {
                // Call the use case to like/unlike the post
                val isLiked = likeUseCase.invoke(postId)
                _likeState.value = LikeState.Success(isLiked)
            } catch (e: Exception) {
                // Handle any exceptions and update the state with an error message
                _likeState.value = LikeState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
    fun unlikePost(postId: Int) {
        viewModelScope.launch {
            _likeState.value = LikeState.Loading
            try {
                // Call the use case to like/unlike the post
                unlikeUseCase.invoke(postId).collect {
                    _likeState.value = LikeState.Idle
                }
            } catch (
                e: Exception
            ) {
                // Handle any exceptions and update the state with an error message
                _likeState.value = LikeState.Error(e.message ?: "An unknown error occurred")
            }

        }
    }

    fun getLike(postId: Int) {
        viewModelScope.launch {
            _likeState.value = LikeState.Loading
            try {
                // Call the use case to like/unlike the post
                getLikeUseCase.invoke(postId)
            }catch (
                e: Exception
            ) {
                _likeState.value = LikeState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}