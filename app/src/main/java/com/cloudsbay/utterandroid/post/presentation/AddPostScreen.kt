package com.cloudsbay.utterandroid.post.presentation

import com.google.common.util.concurrent.ListenableFuture
import android.Manifest
import android.content.ContentResolver
import androidx.camera.core.Camera
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.CameraRear
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lens
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.view.ViewGroup.LayoutParams

@Composable
fun AddPostScreen(
    addPostViewModel: AddPostViewModel = viewModel(
        factory = AddPostViewModelFactory(LocalContext.current.contentResolver)
    ),
    onPostSelected: (Uri) -> Unit,
    navController: NavController
) {
    val cameraSelector by addPostViewModel.cameraSelector.collectAsState()
    val flashEnabled by addPostViewModel.flashEnabled.collectAsState()
    val capturedMedia by addPostViewModel.capturedMedia.collectAsState()
    val galleryImages by addPostViewModel.galleryImages.collectAsState()
    val context = LocalContext.current
    val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(context)
    val cameraProvider = cameraProviderFuture.get()


    val imageCapture = remember { ImageCapture.Builder().build() }
    var camera: Camera? = null

    // Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { addPostViewModel.setCapturedMedia(it) }
    }

    // Permission Launcher for Camera
    val permissionsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // Proceed to open the camera
        } else {
            // Handle the case where the user denies the camera permission
            navController.popBackStack() // This will navigate back to the previous screen (FeedScreen)
        }
    }

    LaunchedEffect(Unit) {
        permissionsLauncher.launch(Manifest.permission.CAMERA)
    }

    // UI Layout
    Column(modifier = Modifier.fillMaxSize()) {
        // Camera Section (Part of scrollable content)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            CameraView(
                cameraExecutor = cameraExecutor,
                imageCapture = imageCapture,
                cameraSelector = cameraSelector,
                onCameraReady = { camera = it },
                flashEnabled = flashEnabled
            )

            // Camera Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { addPostViewModel.toggleCamera() }) {
                        Icon(
                            if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) Icons.Default.CameraFront
                            else Icons.Default.CameraRear,
                            contentDescription = "Switch Camera"
                        )
                    }

                    IconButton(onClick = { addPostViewModel.toggleFlash() }) {
                        Icon(
                            if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Toggle Flash"
                        )
                    }
                }

                Button(
                    onClick = { captureImage(context, imageCapture) { uri -> addPostViewModel.setCapturedMedia(uri) } },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Default.Lens, contentDescription = "Capture Media")
                }
            }
        }
        Spacer(modifier = Modifier.heightIn(min = 16.dp))
        // Gallery Section with Scroll
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            items(galleryImages.size) { index ->
                val uri = galleryImages[index]
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context).data(uri).build()
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxSize()
                        .background(Color.Gray)
                        .clickable { addPostViewModel.setCapturedMedia(uri) }
                )
            }
        }
    }

    // Handle captured or picked media
    LaunchedEffect(capturedMedia) {
        capturedMedia?.let { onPostSelected(it) }
    }

    // Release camera when leaving the screen
    DisposableEffect(navController) {
        onDispose {
            camera?.let {
                it.cameraControl.enableTorch(false)
            }
            cameraProvider.unbindAll()
            cameraExecutor.shutdown()
        }
    }
}

@Composable
fun CameraView(
    cameraExecutor: ExecutorService,
    imageCapture: ImageCapture,
    cameraSelector: CameraSelector,
    onCameraReady: (Camera?) -> Unit,
    flashEnabled: Boolean
) {
    val context = LocalContext.current
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(context)

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                // Crop to a 1:1 aspect ratio
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                ctx as androidx.lifecycle.LifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )

            camera.cameraControl.enableTorch(flashEnabled)
            onCameraReady(camera)

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

private fun captureImage(context: android.content.Context, imageCapture: ImageCapture, onImageCaptured: (Uri) -> Unit) {
    val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture.takePicture(outputFileOptions, Executors.newSingleThreadExecutor(), object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            onImageCaptured(Uri.fromFile(file))
        }

        override fun onError(exception: ImageCaptureException) {
            // Handle error
        }
    })
}

private fun fetchGalleryImages(contentResolver: ContentResolver): List<Uri> {
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

    return images
}

private fun toggleCameraSelector(current: CameraSelector): CameraSelector {
    return if (current == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
    else CameraSelector.DEFAULT_BACK_CAMERA
}