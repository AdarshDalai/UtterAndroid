package com.cloudsbay.utterandroid.common.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PostCard(
    username: String,
    profilePictureUrl: String,
    caption: String,
    mediaUrl: String,
    timestamp: String,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row {
                //Profile Picture
                //Username
            }

            //Media

            //Caption

            //Timestamp

            //Like, comment, share button

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostCardPreview() {
    PostCard(
        username = "John Doe",
        profilePictureUrl = "https://example.com/profile_picture.jpg",
        caption = "This is the content of the post.",
        mediaUrl = "https://example.com/media.jpg",
        timestamp = "2 hours ago",
        onLikeClick = {},
        onCommentClick = {}
    )
}