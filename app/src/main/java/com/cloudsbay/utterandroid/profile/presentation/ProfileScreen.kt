package com.cloudsbay.utterandroid.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Profile Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Profile Picture
            ProfilePicture(userProfile.profilePictureUrl)

            Spacer(modifier = Modifier.width(16.dp))

            // Stats
            ProfileStats(
                postsState = postsState,
                followersState = followersState,
                followingState = followingState
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name and Bio
        Text(text = userProfile.name, style = MaterialTheme.typography.headlineMedium)
        userProfile.bio?.let {
            Text(text = it, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        if (isCurrentUser) {
            Row (horizontalArrangement = Arrangement.SpaceEvenly){
                Button(onClick = onEditProfileClick) {
                    Text("Edit Profile")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onLogoutClick) {
                    Text("Logout") // Add Logout button here
                }
            }
        } else {
            Button(onClick = onFollowUnfollowClick) {
                Text("Follow/Unfollow") // Update this based on the following state
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

@Composable
fun ProfilePicture(imageUrl: String?) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
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
                style = MaterialTheme.typography.headlineMedium
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