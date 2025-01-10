package com.cloudsbay.utterandroid.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cloudsbay.utterandroid.auth.presentation.AuthTabScreen
import com.cloudsbay.utterandroid.auth.presentation.AuthViewModel
import com.cloudsbay.utterandroid.feed.presentation.FeedScreen
import com.cloudsbay.utterandroid.profile.presentation.ProfileScreen
import com.cloudsbay.utterandroid.profile.presentation.ProfileScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    val profileViewModel: ProfileScreenViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val currentUserState = authViewModel.authState.collectAsState()
    val currentUserId = when (val state = currentUserState.value) {
        is AuthViewModel.AuthState.Success -> state.user.user.sub
        else -> null
    }
    Log.d("AppNavGraph", "1. currentUserId: $currentUserId")
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    LaunchedEffect(currentUserId) {
        Log.d("AppNavGraph", "2. currentUserId: $currentUserId")
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
            if (currentRoute != AppNavigation.Auth.route) {
                BottomNavigationBar(navController)
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
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, authViewModel: AuthViewModel= hiltViewModel()) {
    val items = listOf(BottomNavigation.Home, BottomNavigation.Profile)
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    // Observe the authState to get the current user
    val currentUserState = authViewModel.authState.collectAsState()

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = null) },
                label = { Text(item.label) },
                selected = currentRoute?.startsWith(item.route) == true,
                onClick = {
                    when (item) {
                        is BottomNavigation.Home -> {
                            navController.navigate(AppNavigation.Feed.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        is BottomNavigation.Profile -> {
                            // Launch a coroutine to fetch the current user
                            navController.currentBackStackEntry?.let { backStackEntry ->
                                CoroutineScope(Dispatchers.Main).launch {
                                    authViewModel.fetchCurrentUser()

                                    // Observe the result and navigate with the currentUserId
                                    val currentUserId = (currentUserState.value as? AuthViewModel.AuthState.Success)?.user?.user?.sub
                                    currentUserId?.let {
                                        navController.navigate("${AppNavigation.Profile.route}/$it") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

sealed class AppNavigation(val route: String, val label: String) {
    object Auth : AppNavigation("auth", "Auth")
    object Feed : AppNavigation("feed", "Feed")
    object Profile : AppNavigation("profile", "Profile")
}

sealed class BottomNavigation(
    val title: String,
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : BottomNavigation("Home", AppNavigation.Feed.route, AppNavigation.Feed.label, Icons.Default.Home)
    object Profile : BottomNavigation("Profile", AppNavigation.Profile.route, AppNavigation.Profile.label, Icons.Default.Person)
}