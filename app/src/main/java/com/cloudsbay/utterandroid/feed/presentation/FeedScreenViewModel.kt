package com.cloudsbay.utterandroid.feed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudsbay.utterandroid.feed.domain.FeedResponse
import com.cloudsbay.utterandroid.feed.domain.FeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedScreenViewModel @Inject constructor(
    private val feedUseCase: FeedUseCase
): ViewModel() {

    sealed class FeedState {
        data object Idle : FeedState()
        data object Loading : FeedState()
        data class Success(val feed: FeedResponse) : FeedState()
        data class Error(val message: String) : FeedState()
    }

    private val _feedState = MutableStateFlow<FeedState>(FeedState.Idle)
    val feedState: StateFlow<FeedState> = _feedState

    fun getFeed() {
        _feedState.value = FeedState.Loading
        viewModelScope.launch {
            runCatching {
                feedUseCase().collect { feedResponse ->
                    _feedState.value = FeedState.Success(feedResponse)
                }
            }.onFailure {
                _feedState.value = FeedState.Error(it.message ?: "An unexpected error occurred")
            }
        }
    }

}