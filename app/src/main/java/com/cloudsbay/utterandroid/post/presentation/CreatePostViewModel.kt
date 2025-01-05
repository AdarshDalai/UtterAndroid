package com.cloudsbay.utterandroid.post.presentation

import android.net.Uri
import android.provider.MediaStore
import androidx.camera.core.CameraSelector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import android.content.ContentResolver


class CreatePostViewModel(private val contentResolver: ContentResolver) : ViewModel() {
    sealed class CreatePostState {
        data object Idle : CreatePostState()
        data class MediaSelected(val mediaFile: File) : CreatePostState()
        data class Edited(val editedMediaFile: File) : CreatePostState()
        data class Error(val message: String) : CreatePostState()
    }

    private val _createPostState = MutableStateFlow<CreatePostState>(CreatePostState.Idle)
    val createPostState: StateFlow<CreatePostState> = _createPostState

    private val _cameraSelector = MutableStateFlow(CameraSelector.DEFAULT_BACK_CAMERA)
    val cameraSelector: StateFlow<CameraSelector> = _cameraSelector

    private val _flashEnabled = MutableStateFlow(false)
    val flashEnabled: StateFlow<Boolean> = _flashEnabled

    private val _capturedMedia = MutableStateFlow<Uri?>(null)
    val capturedMedia: StateFlow<Uri?> = _capturedMedia

    private val _galleryImages = MutableStateFlow<List<Uri>>(emptyList())
    val galleryImages: StateFlow<List<Uri>> = _galleryImages

    init {
        loadGalleryImages()
    }

    /**
     * Toggle the camera selector between front and back camera.
     */
    fun toggleCamera() {
        _cameraSelector.update {
            if (it == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    /**
     * Toggle flash state on or off.
     */
    fun toggleFlash() {
        _flashEnabled.update { !it }
    }

    /**
     * Set the captured media URI (e.g., after taking a picture).
     */
    fun setCapturedMedia(uri: Uri) {
        _capturedMedia.value = uri

    }

    /**
     * Fetch and load the gallery images into the state.
     */
    private fun loadGalleryImages() {
        val images = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )

        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (it.moveToNext()) {
                val id = it.getLong(columnIndex)
                val uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
                images.add(uri)
            }
        }

        _galleryImages.value = images
    }
    fun selectMediaFromCamera(capturedFile: File) {
        _createPostState.value = CreatePostState.MediaSelected(capturedFile)
    }

    fun selectMediaFromGallery(selectedFile: File) {
        _createPostState.value = CreatePostState.MediaSelected(selectedFile)
    }

    fun editMedia(editedFile: File) {
        _createPostState.value = CreatePostState.Edited(editedFile)
    }

    fun resetState() {
        _createPostState.value = CreatePostState.Idle
    }

    fun handleError(message: String) {
        _createPostState.value = CreatePostState.Error(message)
    }
}