package com.cloudsbay.utterandroid.post.presentation

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.CameraRear
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    viewModel: CreatePostViewModel = hiltViewModel(),
    onNavigateToEdit: (Uri) -> Unit,
    navController: NavController
) {
    val cameraSelector by viewModel.cameraSelector.collectAsState()
    val flashEnabled by viewModel.flashEnabled.collectAsState()
    val capturedMedia by viewModel.capturedMedia.collectAsState()
    val galleryImages by viewModel.galleryImages.collectAsState()
    val context = LocalContext.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val imageCapture = remember { ImageCapture.Builder().build() }
    var camera: Camera? = null

    // Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.setCapturedMedia(it) }
    }

    // Permission Launcher for Camera
    val permissionsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted) {
            navController.popBackStack() // Navigate back if permission is denied
        }
    }

    LaunchedEffect(Unit) {
        permissionsLauncher.launch(Manifest.permission.CAMERA)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Camera Section
        Box(modifier = Modifier.weight(1f)) {
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
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { viewModel.toggleCamera() }) {
                        Icon(
                            if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) Icons.Default.CameraFront
                            else Icons.Default.CameraRear,
                            contentDescription = "Switch Camera"
                        )
                    }
                    IconButton(onClick = { viewModel.toggleFlash() }) {
                        Icon(
                            if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Toggle Flash"
                        )
                    }
                }

                Button(
                    onClick = { captureImage(context, imageCapture) { uri -> viewModel.setCapturedMedia(uri) } },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Capture")
                }
            }
        }

        // Gallery Section
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
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
                        .aspectRatio(1f)
                        .clickable { viewModel.setCapturedMedia(uri) }
                )
            }
        }
    }

    // Handle captured or picked media
    LaunchedEffect(capturedMedia) {
        capturedMedia?.let { onNavigateToEdit(it) }
    }

    DisposableEffect(navController) {
        onDispose {
            camera?.let {
                it.cameraControl.enableTorch(false) // Disable torch when navigating away
            }
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
                layoutParams =
                    FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                ctx as LifecycleOwner,
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

private fun captureImage(context: Context, imageCapture: ImageCapture, onImageCaptured: (Uri) -> Unit) {
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

@Composable
fun CameraWithGallery(
    onPhotoCaptured: (File) -> Unit,
    onGallerySelected: (File) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val outputDirectory = remember { getOutputDirectory(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                val previewView = PreviewView(context).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = androidx.camera.core.Preview.Builder().build().also { // Updated line
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(context))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay with buttons
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Gallery button
                IconButton(
                    onClick = {
                        // Mock gallery selection
                        val file = File("path/to/selected/image.jpg")
                        onGallerySelected(file)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Gallery",
                        tint = Color.White
                    )
                }

                // Capture button
                FloatingActionButton(
                    onClick = {
                        val photoFile = File(
                            outputDirectory,
                            "IMG_${System.currentTimeMillis()}.jpg"
                        )
                        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                        imageCapture.takePicture(
                            outputOptions,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                    onPhotoCaptured(photoFile)
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    exception.printStackTrace()
                                }
                            }
                        )
                    },
                    containerColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Take Photo"
                    )
                }
            }
        }
    }
}

fun getOutputDirectory(context: Context): File {
    val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
        File(it, "Utter").apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
}

@Composable
fun FullScreenCamera(
    onPhotoCaptured: (File) -> Unit,
    onGallerySelected: (File) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Mock CameraX preview
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black), // Simulate a camera preview
            contentAlignment = Alignment.Center
        ) {
            Text("Camera Preview", color = Color.White)
        }

        // Overlay with buttons
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Gallery button
                IconButton(
                    onClick = {
                        // Mock gallery selection
                        val file = File("path/to/selected/image.jpg")
                        onGallerySelected(file)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Gallery",
                        tint = Color.White
                    )
                }

                // Capture button
                FloatingActionButton(
                    onClick = {
                        // Mock photo capture
                        val file = File("path/to/captured/image.jpg")
                        onPhotoCaptured(file)
                    },
                    containerColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Take Photo"
                    )
                }
            }
        }
    }
}

@Composable
fun MediaSelection(
    onCameraSelected: (File) -> Unit,
    onGallerySelected: (File) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            // Mock Camera File Selection
            val file = File("path/to/captured/image.jpg")
            onCameraSelected(file)
        }) {
            Text("Take Photo")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // Mock Gallery File Selection
            val file = File("path/to/selected/image.jpg")
            onGallerySelected(file)
        }) {
            Text("Select from Gallery")
        }
    }
}

@Composable
fun MediaPreview(
    mediaFile: File,
    onEdit: () -> Unit,
    onUpload: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Preview Media",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        // Display mock preview
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text("Media Preview")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onEdit) {
            Text("Edit Media")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onUpload) {
            Text("Upload Media")
        }
    }
}

@Preview
@Composable
fun CreatePostScreenPreview() {
    // Create a mock ViewModel
    val viewModel = CreatePostViewModel(
        contentResolver = LocalContext.current.contentResolver
    )
    CreatePostScreen(
        viewModel = viewModel,
        onNavigateToEdit = { /* Handle navigation to edit screen */ },
        navController = NavController(LocalContext.current),
    )
}