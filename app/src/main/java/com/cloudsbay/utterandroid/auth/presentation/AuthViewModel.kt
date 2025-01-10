package com.cloudsbay.utterandroid.auth.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudsbay.utterandroid.auth.domain.model.CurrentUserResponse
import com.cloudsbay.utterandroid.auth.domain.model.LoginRequest
import com.cloudsbay.utterandroid.auth.domain.model.SignupRequest
import com.cloudsbay.utterandroid.auth.domain.usecase.ClearTokensUseCase
import com.cloudsbay.utterandroid.auth.domain.usecase.GetCurrentUserUseCase
import com.cloudsbay.utterandroid.auth.domain.usecase.LoginUseCase
import com.cloudsbay.utterandroid.auth.domain.usecase.LogoutUseCase
import com.cloudsbay.utterandroid.auth.domain.usecase.SaveCurrentUserIdUseCase
import com.cloudsbay.utterandroid.auth.domain.usecase.SaveTokensUseCase
import com.cloudsbay.utterandroid.auth.domain.usecase.SignupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val saveTokenUseCase: SaveTokensUseCase,
    private val clearTokenUseCase: ClearTokensUseCase,
    private val signupUseCase: SignupUseCase,
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val saveCurrentUserIdUseCase: SaveCurrentUserIdUseCase
) : ViewModel() {

    sealed class AuthState {
        data object Idle : AuthState()
        data object Loading : AuthState()
        data class Success(val user: CurrentUserResponse) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    init {
        viewModelScope.launch {
            runBlocking {
                fetchCurrentUser()
            }
        }
    }

    fun signup(email: String, password: String, username: String, name: String, bio: String, profilePictureUrl: String? = null) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            runCatching {
                signupUseCase(
                    SignupRequest(
                        email = email,
                        password = password,
                        username = username,
                        name = name,
                        bio = bio,
                        profilePictureUrl = profilePictureUrl
                    )
                ).collect { it ->
                    val accessToken = it.session.tokenData.accessToken
                    val refreshToken = it.session.tokenData.refreshToken

                    saveTokenUseCase(accessToken, refreshToken)
                    runBlocking {
                        fetchCurrentUser()
                    }
                }
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "An unexpected error occurred")
            }
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            runCatching {
                // Perform login and save tokens
                loginUseCase(LoginRequest(email, password)).collect { loginResponse ->
                    val accessToken = loginResponse.session.tokenData.accessToken
                    val refreshToken = loginResponse.session.tokenData.refreshToken

                    saveTokenUseCase(accessToken, refreshToken)
                    runBlocking {
                        fetchCurrentUser()
                    }
                }
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "An unexpected error occurred")
            }
        }
    }

    suspend fun fetchCurrentUser() {
        runCatching {
            getCurrentUserUseCase().collect { currentUser ->
                _authState.value = AuthState.Success(currentUser)
                saveCurrentUserIdUseCase(currentUser.user.sub).collect {
                    Log.d("AuthViewModel", "Current user id saved: ${currentUser.user.sub}")
                }
            }
        }.onFailure {
            _authState.value = AuthState.Error(it.message ?: "An error occurred while fetching the user")
        }
    }

    fun logout() {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            runCatching {
                logoutUseCase().collect {
                    clearTokenUseCase()
                    _authState.value = AuthState.Idle
                }
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "An unexpected error occurred")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}