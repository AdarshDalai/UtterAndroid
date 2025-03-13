package com.cloudsbay.utterandroid.navigation

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cloudsbay.utterandroid.auth.presentation.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomNavigation.Home,
        BottomNavigation.CreatePost,
        BottomNavigation.Profile
    )
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var firstPressed by rememberSaveable { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Handle back button press
    BackHandler(enabled = true) {
        when (currentRoute) {
            BottomNavigation.Home.route -> {
                if (firstPressed) {
                    val activity = context as? Activity
                    activity?.finish()
                } else {
                    firstPressed = true
                    Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
                    coroutineScope.launch {
                        delay(2000L)
                        firstPressed = false
                    }
                }
            }
            else -> {
                // Redirect to FeedScreen and clear backstack
                navController.navigate(BottomNavigation.Home.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    BottomAppBar {
        screens.forEach { screen ->
            AddItem(screen = screen, currentDestination = navBackStackEntry?.destination, navController = navController)
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomNavigation,
    currentDestination: NavDestination?,
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val isSelected = when (screen) {
        BottomNavigation.Profile -> currentDestination?.route?.startsWith(screen.route) == true
        else -> currentDestination?.route == screen.route
    }

    Log.d("BottomBarNavigation", "Screen: ${screen.title}, isSelected: $isSelected")

    NavigationBarItem(
        icon = {
            if (screen.badgeCount != null && screen.badgeCount > 0) {
                BadgedBox(badge = { Badge { Text(text = screen.badgeCount.toString()) } }) {
                    Icon(
                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                        contentDescription = screen.route,
                        modifier = Modifier
                            .size(30.dp) // Adjust the size as needed
                            .clip(CircleShape)
                    )
                }
            } else {
                Icon(
                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                    contentDescription = screen.route,
                    modifier = Modifier
                        .size(30.dp) // Adjust the size as needed
                        .clip(CircleShape)
                )
            }
        },
        selected = isSelected,
        onClick = {
            if (screen.route == BottomNavigation.Profile.route) {
                navController.currentBackStackEntry?.let { backStackEntry ->
                    CoroutineScope(Dispatchers.Main).launch {
                        authViewModel.fetchCurrentUser()
                        val currentUserId = (authViewModel.authState.value as? AuthViewModel.AuthState.Success)?.user?.user?.sub
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
            } else if (!isSelected) {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                Log.d("NavBackStack", "Navigated to: ${screen.route}")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    BottomBar(navController = NavHostController(LocalContext.current))
}