package com.cloudsbay.utterandroid.post.presentation

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.cloudsbay.utterandroid.navigation.BottomNavigation

@Composable
fun UploadPostScreen(
    mediaUri: Uri?,
    navController: NavController,
    viewModel: UploadPostViewModel = hiltViewModel()
) {
    val caption by viewModel.caption.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()
    val uploadMessage by viewModel.uploadMessage.collectAsState()

    var showUploadDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Handle back press for exit confirmation
    BackHandler { showExitDialog = true }

    if (showExitDialog) {
        ConfirmDialog(
            title = "Confirm Exit",
            text = "Are you sure you want to exit? Any unsaved changes will be lost.",
            onConfirm = {
                navController.navigate(BottomNavigation.Home.route) {
                    popUpTo(0) // Clears the back stack
                }
                showExitDialog = false
            },
            onDismiss = { showExitDialog = false }
        )
    }

    if (showUploadDialog) {
        ConfirmDialog(
            title = "Confirm Upload",
            text = "Are you sure you want to upload this post?",
            onConfirm = {
                viewModel.uploadPost(mediaUri, caption, context)
                showUploadDialog = false
            },
            onDismiss = { showUploadDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Preview Your Post",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Media Preview
        if (mediaUri != null) {
            Image(
                painter = rememberAsyncImagePainter(mediaUri),
                contentDescription = "Image Preview",
                modifier = Modifier
                    .size(300.dp)
                    .padding(8.dp)
            )
        } else {
            Text(
                text = "No media selected.",
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Caption Input
        OutlinedTextField(
            value = caption,
            onValueChange = viewModel::updateCaption,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Write a caption...") },
            maxLines = 3,
            singleLine = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Upload Button
        Button(
            onClick = { if (!isUploading) showUploadDialog = true },
            enabled = !isUploading && mediaUri != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isUploading) "Uploading..." else "Upload Post")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cancel Button
        OutlinedButton(
            onClick = { showExitDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Upload Result
        uploadMessage?.let {
            Text(
                text = it,
                color = if (it.contains("success", true)) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}