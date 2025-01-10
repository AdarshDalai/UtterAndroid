package com.cloudsbay.utterandroid.feed.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

import androidx.compose.foundation.clickable
import androidx.navigation.NavController
import com.cloudsbay.utterandroid.navigation.AppNavigation

@Composable
fun PostComposable(
    postId: Int,
    userId: String,
    content: String,
    mediaUrl: String,
    createdAt: String,
    navController: NavController, // Pass NavController as a parameter
    postViewModel: PostViewModel = hiltViewModel() // Injected ViewModel
) {
    val profileState by postViewModel.profileState.collectAsState()

    // Trigger profile fetch when the composable is launched
    LaunchedEffect(userId) {
        postViewModel.fetchUserProfile(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Post header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clickable {
                    // Navigate to the profile screen
                    navController.navigate("${AppNavigation.Profile.route}/$userId")                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User avatar
            AsyncImage(
                model = profileState?.profilePictureUrl ?: "https://via.placeholder.com/150",
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Username and timestamp
            Column {
                Text(
                    text = profileState?.username ?: "Loading...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = createdAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6F)
                )
            }
        }

        // Post media
        if (mediaUrl.isNotEmpty()) {
            AsyncImage(
                model = mediaUrl,
                contentDescription = "Post Media",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(RoundedCornerShape(0.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Handle Like */ }) {
                Icon(
                    Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = { /* Handle Comment */ }) {
                Icon(
                    Icons.AutoMirrored.Outlined.Comment,
                    contentDescription = "Comment",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = { /* Handle Share */ }) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "Share",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Post caption/content
        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(
                text = profileState?.username ?: "Loading...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}