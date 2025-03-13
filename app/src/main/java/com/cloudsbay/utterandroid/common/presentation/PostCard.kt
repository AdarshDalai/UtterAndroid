package com.cloudsbay.utterandroid.common.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.cloudsbay.utterandroid.like.presentation.LikeViewModel
import com.cloudsbay.utterandroid.navigation.AppNavigation

@Composable
fun PostCard(
    postId: Int,
    userId: String,
    content: String,
    mediaUrl: String,
    createdAt: String,
    navController: NavController,
    postViewModel: PostViewModel = hiltViewModel(),
) {
    val profileState by postViewModel.profileState.collectAsState()
    var isCaptionVisible by remember { mutableStateOf(false) }

    // Fetch the profile when this Composable is launched
    LaunchedEffect(userId) {
        postViewModel.fetchUserProfile(userId)
    }

    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(50.dp)
    ) {
        Column{
            Box {
                PostMedia(
                    mediaUrl = mediaUrl,
                    content = content,
                    isCaptionVisible = isCaptionVisible,
                    onMediaClick = { isCaptionVisible = !isCaptionVisible }
                )
                Column {
                    PostHeader(
                        userId = userId,
                        createdAt = createdAt,
                        username = profileState?.username,
                        profilePictureUrl = profileState?.profilePictureUrl,
                        navController = navController
                    )
                }
            }
            PostActions(postId = postId)
        }
    }
}

@Composable
private fun PostMedia(
    mediaUrl: String,
    content: String,
    isCaptionVisible: Boolean,
    onMediaClick: () -> Unit
) {
    // Animate the visibility of the caption
    val alpha by animateFloatAsState(
        targetValue = if (isCaptionVisible) 0.8f else 0.0f,
        animationSpec = tween(durationMillis = 300) // Smooth transition duration
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clickable { onMediaClick() }
    ) {
        AsyncImage(
            model = mediaUrl,
            contentDescription = "Post Media",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        // Gradient and caption overlay with animation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.0f),
                            MaterialTheme.colorScheme.surface.copy(alpha = alpha)
                        )
                    )
                )
        ) {
            if (alpha > 0.0f) { // Only show text when gradient is visible
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomStart),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun PostHeader(
    userId: String,
    createdAt: String,
    username: String?,
    profilePictureUrl: String?,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Adjust the height to fit the content
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.8f),  // Transparent black at the top
                        Color.Black.copy(alpha = 0.0f)   // Semi-transparent black at the bottom
                    )
                )
            )
            .clickable {
                navController.navigate("${AppNavigation.Profile.route}/$userId")
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = profilePictureUrl ?: "https://via.placeholder.com/150",
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = username ?: "Loading...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Text(
                    text = createdAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun PostActions(
    postId: Int,
    viewModel: LikeViewModel = hiltViewModel(),
    onLikeClick: () -> Unit = {}
) {
    val likeState by viewModel.likeState.collectAsState()

    // Track the like/unlike state from the LikeState
    val isLiked = when (likeState) {
        is LikeViewModel.LikeState.Success -> {
            // Check if the post is liked based on the response
            (likeState as LikeViewModel.LikeState.Success).isLiked
        }
        else -> false // Default to false when idle or loading
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Like Button - Toggle like/unlike based on current state
        IconButton(onClick = {
            if (isLiked as Boolean) {
                viewModel.unlikePost(postId) // Unlike if already liked
            } else {
                viewModel.likePost(postId) // Like if not liked
            }
            onLikeClick() // Optionally trigger callback
        }) {
            Icon(
                imageVector = if (isLiked as Boolean) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isLiked) "Unlike" else "Like",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Comment Button
        IconButton(onClick = { /* Handle Comment */ }) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Comment,
                contentDescription = "Comment",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Share Button
        IconButton(onClick = { /* Handle Share */ }) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = "Share",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    // Optionally, handle any loading or error states in UI
    when (likeState) {
        is LikeViewModel.LikeState.Loading -> {
            // Show a loading indicator if needed
            CircularProgressIndicator()
        }
        is LikeViewModel.LikeState.Error -> {
            // Handle any error state (e.g., show a snackbar)
            val errorMessage = (likeState as LikeViewModel.LikeState.Error).errorMessage
            Text(text = "Error: $errorMessage", color = Color.Red)
        }
        else -> {}
    }
}

@Preview(showBackground = true)
@Composable
fun PostCardPreview() {
    PostCard(
        postId = 1,
        userId = "user123",
        content = "This is a sample post content.",
        mediaUrl = "https://via.placeholder.com/150",
        createdAt = "Just now",
        navController = NavController(LocalContext.current)
    )
}