package com.cloudsbay.utterandroid.profile.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudsbay.utterandroid.auth.domain.model.CurrentUserResponse
import com.cloudsbay.utterandroid.auth.domain.usecase.ClearTokensUseCase
import com.cloudsbay.utterandroid.auth.domain.usecase.GetCurrentUserUseCase
import com.cloudsbay.utterandroid.auth.domain.usecase.LogoutUseCase
import com.cloudsbay.utterandroid.auth.presentation.AuthViewModel.AuthState
import com.cloudsbay.utterandroid.post.domain.model.PostsResponse
import com.cloudsbay.utterandroid.profile.domain.model.ProfileResponse
import com.cloudsbay.utterandroid.profile.domain.model.UserFollowerResponse
import com.cloudsbay.utterandroid.profile.domain.model.UserFollowingResponse
import com.cloudsbay.utterandroid.profile.domain.usecase.GetCurrentUserIdUseCase
import com.cloudsbay.utterandroid.profile.domain.usecase.GetUserFollowingUseCase
import com.cloudsbay.utterandroid.profile.domain.usecase.ProfilePostsUseCase
import com.cloudsbay.utterandroid.profile.domain.usecase.ProfilesUseCase
import com.cloudsbay.utterandroid.profile.domain.usecase.UserFollowersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val getUserFollowingUseCase: GetUserFollowingUseCase,
    private val profilePostsUseCase: ProfilePostsUseCase,
    private val profilesUseCase: ProfilesUseCase,
    private val userFollowersUseCase: UserFollowersUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val clearTokensUseCase: ClearTokensUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : ViewModel() {

    sealed class ProfileState {
        data object Idle : ProfileState()
        data object Loading : ProfileState()
        data class Success(val profile: ProfileResponse.UserProfile) : ProfileState()
        data class Error(val message: String) : ProfileState()
    }

    sealed class PostsState {
        data object Idle : PostsState()
        data object Loading : PostsState()
        data class Success(val posts: List<PostsResponse.Post>) : PostsState()
        data class Error(val message: String) : PostsState()
    }

    sealed class FollowersState {
        data object Idle : FollowersState()
        data object Loading : FollowersState()
        data class Success(val followers: List<UserFollowerResponse.Follower>) : FollowersState()
        data class Error(val message: String) : FollowersState()
    }

    sealed class FollowingState {
        data object Idle : FollowingState()
        data object Loading : FollowingState()
        data class Success(val following: List<UserFollowingResponse.Following>) : FollowingState()
        data class Error(val message: String) : FollowingState()
    }

    sealed class CurrentUserState {
        data object Idle : CurrentUserState()
        data object Loading : CurrentUserState()
        data class Success(val currentUser: CurrentUserResponse) : CurrentUserState()
        data class Error(val message: String) : CurrentUserState()
    }

    sealed class LogoutState {
        object Idle : LogoutState()
        object Loading : LogoutState()
        object Success : LogoutState()
        data class Error(val message: String) : LogoutState()
    }

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState.asStateFlow()

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _postsState = MutableStateFlow<PostsState>(PostsState.Idle)
    val postsState: StateFlow<PostsState> = _postsState.asStateFlow()

    private val _followersState = MutableStateFlow<FollowersState>(FollowersState.Idle)
    val followersState: StateFlow<FollowersState> = _followersState.asStateFlow()

    private val _followingState = MutableStateFlow<FollowingState>(FollowingState.Idle)
    val followingState: StateFlow<FollowingState> = _followingState.asStateFlow()

    private val _currentUserState = MutableStateFlow<CurrentUserState>(CurrentUserState.Idle)
    val currentUserState: StateFlow<CurrentUserState> = _currentUserState.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            fetchCurrentUser()
        }
    }

    suspend fun fetchCurrentUser() {
        _currentUserState.value = CurrentUserState.Loading
        runCatching {
            getCurrentUserUseCase().collect { currentUser ->
                _currentUserState.value = CurrentUserState.Success(currentUser)
                Log.d("ProfileScreenViewModel", "Current user: ${currentUser.user.sub}")
            }
        }.onFailure {
            _currentUserState.value = CurrentUserState.Error(it.message ?: "An error occurred while fetching the user")
        }
    }

    suspend fun getProfile(userId: String) {
        _profileState.value = ProfileState.Loading
        runCatching {
            profilesUseCase(userId).collect { profile ->
                _profileState.value = ProfileState.Success(profile.user)
            }
        }.onFailure {
            _profileState.value = ProfileState.Error(it.message ?: "An error occurred while fetching the profile")
        }
    }

    suspend fun getPostsOfUser(userId: String) {
        _postsState.value = PostsState.Loading
        runCatching {
            profilePostsUseCase(userId).collect { posts ->
                _postsState.value = PostsState.Success(posts.posts)
            }
        }.onFailure {
            _postsState.value = PostsState.Error(it.message ?: "An error occurred while fetching the posts")
        }
    }

    suspend fun getUserFollowers(userId: String) {
        _followersState.value = FollowersState.Loading
        runCatching {
            userFollowersUseCase(userId).collect {
                _followersState.value = FollowersState.Success(it.followers)
            }
        }.onFailure {
            _followersState.value = FollowersState.Error(it.message ?: "An error occurred while fetching the followers")
        }
    }

    suspend fun getUserFollowing(userId: String) {
        _followingState.value = FollowingState.Loading
        runCatching {
            getUserFollowingUseCase(userId).collect {
                _followingState.value = FollowingState.Success(it.following)
            }
        }.onFailure {
            _followingState.value = FollowingState.Error(it.message ?: "An error occurred while fetching the following")
        }
    }

    private fun resetState() {
        _profileState.value = ProfileState.Idle
        _postsState.value = PostsState.Idle
        _followersState.value = FollowersState.Idle
        _followingState.value = FollowingState.Idle
        _currentUserState.value = CurrentUserState.Idle
    }

    fun logout() {
        _logoutState.value = LogoutState.Loading
        viewModelScope.launch {
            runCatching {
                logoutUseCase().collect {
                    clearTokensUseCase()
                    resetState()
                    _logoutState.value = LogoutState.Success
                }
            }.onFailure {
                _logoutState.value = LogoutState.Error(it.message ?: "An unexpected error occurred")
            }
        }
    }
}