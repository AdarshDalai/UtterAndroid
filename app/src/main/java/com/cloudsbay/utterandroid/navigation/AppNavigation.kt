package com.cloudsbay.utterandroid.navigation

import android.graphics.drawable.Icon
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cloudsbay.utterandroid.auth.presentation.AuthTabScreen
import com.cloudsbay.utterandroid.auth.presentation.AuthViewModel
import com.cloudsbay.utterandroid.feed.presentation.FeedScreen
import com.cloudsbay.utterandroid.post.presentation.AddPostScreen
import com.cloudsbay.utterandroid.post.presentation.UploadPostScreen
import com.cloudsbay.utterandroid.profile.presentation.ProfileScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun AppNavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val currentUserState = authViewModel.authState.collectAsState()
    val currentUserId = when (val state = currentUserState.value) {
        is AuthViewModel.AuthState.Success -> state.user.user.sub
        else -> null
    }

    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            navController.navigate(AppNavigation.Feed.route) {
                popUpTo(AppNavigation.Auth.route) { inclusive = true }
            }
        } else {
            navController.navigate(AppNavigation.Auth.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (navController.currentBackStackEntryAsState().value?.destination?.route != AppNavigation.Auth.route) {
                BottomBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppNavigation.Auth.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppNavigation.Auth.route) {
                AuthTabScreen(navController)
            }
            composable(AppNavigation.Feed.route) {
                FeedScreen(navController = navController)
            }
            composable("${AppNavigation.Profile.route}/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")
                if (userId != null) {
                    ProfileScreen(userId = userId, navController = navController)
                }
            }
            composable(AppNavigation.CreatePost.route) {
                AddPostScreen(
                    navController = navController,
                    onPostSelected = { uri ->
                        navController.navigate("${AppNavigation.UploadPost.route}?uri=$uri")
                    }
                )
            }
            composable("${AppNavigation.UploadPost.route}?uri={uri}") { backStackEntry ->
                val mediaUri = backStackEntry.arguments?.getString("uri")
                UploadPostScreen(
                    mediaUri = Uri.parse(mediaUri),
                    navController = navController
                )
            }
        }
    }
}

sealed class AppNavigation(val route: String, val label: String) {
    object Auth : AppNavigation("auth", "Auth")
    object Feed : AppNavigation("feed", "Feed")
    object Profile : AppNavigation("profile", "Profile")
    object CreatePost : AppNavigation("createPost", "Create")
    object UploadPost: AppNavigation("uploadPost", "Upload")
}

sealed class BottomNavigation(
    val title: String,
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int? = null
) {
    object Home : BottomNavigation("Home", AppNavigation.Feed.route, AppNavigation.Feed.label, Icons.Default.Home, Icons.Outlined.Home)
    object Profile : BottomNavigation("Profile", AppNavigation.Profile.route, AppNavigation.Profile.label, Icons.Default.Person, Icons.Outlined.Person)
    object CreatePost: BottomNavigation("CreatePost", AppNavigation.CreatePost.route, AppNavigation.CreatePost.label, Icons.Default.AddCircle, Icons.Default.AddCircleOutline)
}