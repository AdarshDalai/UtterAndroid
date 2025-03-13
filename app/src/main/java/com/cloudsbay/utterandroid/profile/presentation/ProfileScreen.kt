package com.cloudsbay.utterandroid.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.cloudsbay.utterandroid.navigation.AppNavigation
import com.cloudsbay.utterandroid.post.domain.model.PostsResponse
import com.cloudsbay.utterandroid.profile.domain.model.ProfileResponse

@Composable
fun ProfileScreen(
    userId: String, // User ID of the profile to view
    viewModel: ProfileScreenViewModel = hiltViewModel(),
    navController: NavController // Add NavController here for navigation
) {
    val profileState by viewModel.profileState.collectAsState()
    val postsState by viewModel.postsState.collectAsState()
    val currentUserState by viewModel.currentUserState.collectAsState()
    val followersState by viewModel.followersState.collectAsState()
    val followingState by viewModel.followingState.collectAsState()
    val logoutState by viewModel.logoutState.collectAsState()

    val isCurrentUser = currentUserState.let {
        it is ProfileScreenViewModel.CurrentUserState.Success && it.currentUser.user.sub == userId
    }

    LaunchedEffect(userId) {
        viewModel.apply {
            fetchCurrentUser()
            getProfile(userId)
            getPostsOfUser(userId)
            getUserFollowers(userId)
            getUserFollowing(userId)
        }
    }

    // Observe logout state to navigate to AuthScreen
    LaunchedEffect(logoutState) {
        if (logoutState is ProfileScreenViewModel.LogoutState.Success) {
            // Navigate to AuthScreen after logout
            navController.navigate(AppNavigation.Auth.route) {
                popUpTo(AppNavigation.Profile.route) { inclusive = true } // Pop the profile screen from the stack
            }
        }
    }

    when (profileState) {
        is ProfileScreenViewModel.ProfileState.Loading -> {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        }
        is ProfileScreenViewModel.ProfileState.Success -> {
            val userProfile = (profileState as ProfileScreenViewModel.ProfileState.Success).profile
            ProfileContent(
                userProfile = userProfile,
                postsState = postsState,
                followersState = followersState,
                followingState = followingState,
                isCurrentUser = isCurrentUser,
                onFollowUnfollowClick = { /* Add Follow/Unfollow Logic */ },
                onEditProfileClick = { /* Navigate to Edit Profile */ },
                onLogoutClick = { viewModel.logout() } // Handle logout button click
            )
        }
        is ProfileScreenViewModel.ProfileState.Error -> {
            Text(
                text = (profileState as ProfileScreenViewModel.ProfileState.Error).message,
                color = Color.Red,
                modifier = Modifier.fillMaxSize()
            )
        }
        else -> {}
    }
}

@Composable
fun ProfileContent(
    userProfile: ProfileResponse.UserProfile,
    postsState: ProfileScreenViewModel.PostsState,
    followersState: ProfileScreenViewModel.FollowersState,
    followingState: ProfileScreenViewModel.FollowingState,
    isCurrentUser: Boolean,
    onFollowUnfollowClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onLogoutClick: () -> Unit // Add logout click handler
) {

    var showSettingsMenu by remember { mutableStateOf(false) }

    // Profile Header
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .clip(
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 30.dp,
                        bottomEnd = 30.dp
                    )
                )
        ) {
            ProfilePicture(userProfile.profilePictureUrl)

            Row (
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.0f),  // Transparent black at the top
                                Color.Black.copy(alpha = 0.8f)   // Semi-transparent black at the bottom
                            )
                        )
                    )
            ){
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    // Name and Bio
                    Text(
                        text = userProfile.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    userProfile.bio?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
            if(isCurrentUser) {
                IconButton(
                    onClick = { showSettingsMenu = !showSettingsMenu },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(
                            Color.White,
                            shape = CircleShape
                        ) // Circle background for the button
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.Black
                    )
                }
            }
            // Settings Menu Pop-Up
            if (showSettingsMenu) {
                SettingsMenu(onLogoutClick = onLogoutClick, onDismiss = { showSettingsMenu = false })
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Stats
                ProfileStats(
                    postsState = postsState,
                    followersState = followersState,
                    followingState = followingState
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                if (isCurrentUser) {
                    Button(
                        onClick = onEditProfileClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                            )
                            Text(
                                text = "Edit Profile",
                                modifier = Modifier.padding(8.dp),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Button(onClick = onFollowUnfollowClick) {
                        Row {

                            Text(
                                text = "Follow/Unfollow",
                                modifier = Modifier.padding(8.dp),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ) // Update this based on the following state
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Posts Grid
            when (postsState) {
                is ProfileScreenViewModel.PostsState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                is ProfileScreenViewModel.PostsState.Success -> {
                    PostsGrid(posts = (postsState as ProfileScreenViewModel.PostsState.Success).posts)
                }

                is ProfileScreenViewModel.PostsState.Error -> {
                    Text(
                        text = (postsState as ProfileScreenViewModel.PostsState.Error).message,
                        color = Color.Red
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
fun SettingsMenu(
    onLogoutClick: () -> Unit,
    onDismiss: () -> Unit
) {
    // Simple popup menu for settings
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .padding(32.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(32.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { onLogoutClick(); onDismiss() },
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Logout")
            }
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    }
}

@Composable
fun ProfilePicture(imageUrl: String?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        if (imageUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun ProfileStats(
    postsState: ProfileScreenViewModel.PostsState,
    followersState: ProfileScreenViewModel.FollowersState,
    followingState: ProfileScreenViewModel.FollowingState
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (postsState is ProfileScreenViewModel.PostsState.Success) {
                    postsState.posts.size.toString()
                } else {
                    "0"
                },
                style = MaterialTheme.typography.headlineLarge
            )
            Text(text = "Posts", style = MaterialTheme.typography.bodyMedium)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (followersState is ProfileScreenViewModel.FollowersState.Success) {
                    followersState.followers.size.toString()
                } else {
                    "0"
                },
                style = MaterialTheme.typography.headlineLarge
            )
            Text(text = "Followers", style = MaterialTheme.typography.bodyMedium)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (followingState is ProfileScreenViewModel.FollowingState.Success) {
                    followingState.following.size.toString()
                } else {
                    "0"
                },
                style = MaterialTheme.typography.headlineLarge
            )
            Text(text = "Following", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun PostsGrid(posts: List<PostsResponse.Post>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize()
    ) {
        items(posts) { post ->
            Box(modifier = Modifier.padding(4.dp)) {
                Image(
                    painter = rememberAsyncImagePainter(post.mediaUrl),
                    contentDescription = "Post Image",
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}