package com.cloudsbay.utterandroid.post.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudsbay.utterandroid.post.domain.usecase.UploadPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadPostViewModel @Inject constructor(
    private val uploadPostUseCase: UploadPostUseCase
) : ViewModel() {

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress

    private val _caption = MutableStateFlow("")
    val caption: StateFlow<String> = _caption

    private val _uploadMessage = MutableStateFlow<String?>(null)
    val uploadMessage: StateFlow<String?> = _uploadMessage

    fun updateCaption(newCaption: String) {
        _caption.value = newCaption
    }

    fun uploadPost(mediaUri: Uri?, caption: String, context: Context) {
        if (mediaUri == null) {
            _uploadMessage.value = "Media file is required"
            return
        }

        viewModelScope.launch {
            _isUploading.value = true
            _uploadMessage.value = null

            try {
                val response = uploadPostUseCase(caption, mediaUri)  // Pass caption and file
                response.collect { post ->
                    _isUploading.value = false
                    _uploadMessage.value = "Upload successful! Post ID: ${post.id}"
                }
            } catch (e: Exception) {
                _isUploading.value = false
                _uploadMessage.value = "Upload failed: ${e.message}"
            }
        }
    }


}