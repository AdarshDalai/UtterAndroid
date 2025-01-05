package com.cloudsbay.utterandroid.feed.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.cloudsbay.utterandroid.auth.presentation.ErrorScreen
import com.cloudsbay.utterandroid.auth.presentation.LoadingScreen

@Composable
fun FeedScreen(
    viewModel: FeedScreenViewModel = hiltViewModel()
) {
    val feedState by viewModel.feedState.collectAsState()

    when (feedState) {
        is FeedScreenViewModel.FeedState.Idle -> viewModel.getFeed()
        is FeedScreenViewModel.FeedState.Loading -> LoadingScreen()
        is FeedScreenViewModel.FeedState.Success -> {
            val feed = (feedState as FeedScreenViewModel.FeedState.Success).feed
            FeedScreen()
        }
        is FeedScreenViewModel.FeedState.Error -> {
            val errorMessage = (feedState as FeedScreenViewModel.FeedState.Error).message
            ErrorScreen(
                errorMessage = errorMessage,
                onRetryClick = viewModel::getFeed
            )
        }
    }
}