package com.cloudsbay.utterandroid.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudsbay.utterandroid.post.domain.model.PostsResponse
import com.cloudsbay.utterandroid.profile.domain.model.ProfileResponse
import com.cloudsbay.utterandroid.profile.domain.model.UserFollowerResponse
import com.cloudsbay.utterandroid.profile.domain.model.UserFollowingResponse
import com.cloudsbay.utterandroid.profile.domain.usecase.GetUserFollowingUseCase
import com.cloudsbay.utterandroid.profile.domain.usecase.ProfilePostsUseCase
import com.cloudsbay.utterandroid.profile.domain.usecase.ProfilesUseCase
import com.cloudsbay.utterandroid.profile.domain.usecase.UserFollowersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val getUserFollowingUseCase: GetUserFollowingUseCase,
    private val profilePostsUseCase: ProfilePostsUseCase,
    private val profilesUseCase: ProfilesUseCase,
    private val userFollowersUseCase: UserFollowersUseCase
) : ViewModel() {

    // State for the current user ID
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    // State for the profile being viewed
    private val _viewingUserId = MutableStateFlow<String?>(null)
    val viewingUserId: StateFlow<String?> = _viewingUserId.asStateFlow()

    // State for profile data
    private val _profileData = MutableStateFlow<ProfileResponse?>(null)
    val profileData: StateFlow<ProfileResponse?> = _profileData.asStateFlow()

    // State for user posts
    private val _userPosts = MutableStateFlow<PostsResponse?>(null)
    val userPosts: StateFlow<PostsResponse?> = _userPosts.asStateFlow()

    // State for user followers
    private val _userFollowers = MutableStateFlow<UserFollowerResponse?>(null)
    val userFollowers: StateFlow<UserFollowerResponse?> = _userFollowers.asStateFlow()

    // State for user following
    private val _userFollowing = MutableStateFlow<UserFollowingResponse?>(null)
    val userFollowing: StateFlow<UserFollowingResponse?> = _userFollowing.asStateFlow()

    // UI State to differentiate between current user and other user profiles
    val isCurrentUserViewingProfile: StateFlow<Boolean> =
        combine(_currentUserId, _viewingUserId) { currentUserId, viewingUserId ->
            currentUserId != null && currentUserId == viewingUserId
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    /**
     * Sets the current user ID of the application.
     */
    fun setCurrentUser(userId: String) {
        _currentUserId.value = userId
    }

    /**
     * Sets the user ID for the profile being viewed.
     */
    fun setViewingUser(userId: String) {
        _viewingUserId.value = userId
        loadProfileData(userId)
        loadUserPosts(userId)
        loadUserFollowers(userId)
        loadUserFollowing(userId)
    }

    private fun loadProfileData(userId: String) {
        viewModelScope.launch {
            profilesUseCase(userId)
                .catch { throwable ->
                    // Handle errors here
                }
                .collect { response ->
                    _profileData.value = response
                }
        }
    }

    private fun loadUserPosts(userId: String) {
        viewModelScope.launch {
            profilePostsUseCase(userId)
                .catch { throwable ->
                    // Handle errors here
                }
                .collect { response ->
                    _userPosts.value = response
                }
        }
    }

    private fun loadUserFollowers(userId: String) {
        viewModelScope.launch {
            userFollowersUseCase(userId)
                .catch { throwable ->
                    // Handle errors here
                }
                .collect { response ->
                    _userFollowers.value = response
                }
        }
    }

    private fun loadUserFollowing(userId: String) {
        viewModelScope.launch {
            getUserFollowingUseCase(userId)
                .catch { throwable ->
                    // Handle errors here
                }
                .collect { response ->
                    _userFollowing.value = response
                }
        }
    }
}