package com.cloudsbay.utterandroid.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.cloudsbay.utterandroid.post.domain.model.PostsResponse

@Composable
fun ProfileScreen(
    viewModel: ProfileScreenViewModel = hiltViewModel(),
    onEditProfile: () -> Unit,
    onFollowUser: (String) -> Unit,
    onPostClick: (PostsResponse.Post) -> Unit
) {
    val isCurrentUser by viewModel.isCurrentUserViewingProfile.collectAsState()
    val profileData by viewModel.profileData.collectAsState()
    val userPosts by viewModel.userPosts.collectAsState()

    Column {
        profileData?.let { profile ->
            Text(text = profile.user.name)

            if (isCurrentUser) {
                Button(onClick = onEditProfile) {
                    Text("Edit Profile")
                }
            } else {
                Button(onClick = { onFollowUser(profile.user.id) }) {
                    Text("Follow")
                }
            }
        }

        LazyColumn {
            items(userPosts?.posts ?: emptyList()) { post ->
                PostItem(post, onClick = { onPostClick(post) })
            }
        }
    }
}

@Composable
fun PostItem(
    post: PostsResponse.Post,
    onClick: (PostsResponse.Post) -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(post) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.background(Color.White)
        ) {
            // Post Image
            Image(
                painter = rememberAsyncImagePainter(post.mediaUrl),
                contentDescription = "Post Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Post Caption
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        onEditProfile = {},
        onFollowUser = {},
        onPostClick = {}
    )
}