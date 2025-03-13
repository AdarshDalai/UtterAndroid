package com.cloudsbay.utterandroid.feed.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cloudsbay.utterandroid.auth.presentation.ErrorScreen
import com.cloudsbay.utterandroid.auth.presentation.LoadingScreen
import com.cloudsbay.utterandroid.common.presentation.PostCard
import com.cloudsbay.utterandroid.post.domain.model.PostsResponse

@Composable
fun FeedScreen(navController: NavController, viewModel: FeedScreenViewModel = hiltViewModel()) {
    val feedState by viewModel.feedState.collectAsState()

    when (feedState) {
        is FeedScreenViewModel.FeedState.Idle -> viewModel.getFeed()
        is FeedScreenViewModel.FeedState.Loading -> LoadingScreen()
        is FeedScreenViewModel.FeedState.Success -> {
            val feed = (feedState as FeedScreenViewModel.FeedState.Success).feed
            if (feed.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Your feed is empty. Follow people to see their posts!")
                }
            } else {
                FeedContent(feed = feed, navController = navController)
            }
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

@Composable
fun FeedContent(feed: List<PostsResponse.Post>, navController: NavController) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(feed) { post ->
            PostCard(
                postId = post.id,
                userId = post.userId,
                content = post.content,
                mediaUrl = post.mediaUrl,
                createdAt = post.createdAt,
                postViewModel = hiltViewModel(), // Inject PostViewModel for each PostComposable
                navController = navController
            )
        }
    }
}