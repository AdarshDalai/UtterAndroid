package com.cloudsbay.utterandroid.auth.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudsbay.utterandroid.auth.data.repository.AuthRepository
import com.cloudsbay.utterandroid.auth.domain.model.CurrentUserResponse
import com.cloudsbay.utterandroid.auth.domain.model.LoginRequest
import com.cloudsbay.utterandroid.auth.domain.usecase.ClearTokensUseCase
import com.cloudsbay.utterandroid.auth.domain.usecase.GetCurrentUserUseCase
import com.cloudsbay.utterandroid.auth.domain.usecase.LoginUseCase
import com.cloudsbay.utterandroid.auth.domain.usecase.LogoutUseCase
import com.cloudsbay.utterandroid.auth.domain.usecase.SaveTokensUseCase
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
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    sealed class AuthState {
        data object Idle : AuthState()
        data object Loading : AuthState()
        data class Success(val user: CurrentUserResponse) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

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

    private suspend fun fetchCurrentUser() {
        runCatching {
            getCurrentUserUseCase().collect { currentUser ->
                _authState.value = AuthState.Success(currentUser)
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