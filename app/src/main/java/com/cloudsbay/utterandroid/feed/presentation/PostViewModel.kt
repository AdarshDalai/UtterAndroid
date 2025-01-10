package com.cloudsbay.utterandroid.feed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudsbay.utterandroid.profile.domain.model.ProfileResponse
import com.cloudsbay.utterandroid.profile.domain.usecase.ProfilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val profilesUseCase: ProfilesUseCase
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileResponse.UserProfile?>(null)
    val profileState: StateFlow<ProfileResponse.UserProfile?> = _profileState

    fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            profilesUseCase(userId).collect { response ->
                _profileState.value = response.user
            }
        }
    }
}