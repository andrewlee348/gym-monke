package com.example.poopoo.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import bemonke.models.Post
import bemonke.models.likePostPayload
import com.example.poopoo.PreferencesManager
import com.example.poopoo.api.bananaPost
import com.example.poopoo.api.likePost
import com.example.poopoo.navigation.PostImage
import com.example.poopoo.navigation.ProfileImage
import com.example.poopoo.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PostComponent(
    post: Post,
    navController : NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val myUserIdState = remember { mutableStateOf(preferencesManager.getData("userId", "")) }

    // Access the actual String value
    val myUserId = myUserIdState.value

    var bananas by remember { mutableIntStateOf(post.banana)}
    var likeCount by remember { mutableStateOf(post.userLikedList.size) }
    var commentCount by remember { mutableStateOf(post.comments.size) }
    var isCommentModalOpen by remember { mutableStateOf(false) }
    Card(
        modifier =  modifier
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth().clickable {
                navController.navigate(route= Screen.ProfileScreen.route+"?userId="+post.userId)
//                navController.navigate(route= Screen.ProfileScreen.route)
            },
                horizontalArrangement = Arrangement.Center) {
                ProfileImage(post)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = post.username,
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = post.time,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (post.userId == myUserId) {
                    PostEditMenu(post)
                }
            }
            Text(
                text = post.caption,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )
            PostImage(post, bananas)
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ElevatedButton(
                    onClick = {
                        val payload = likePostPayload(
                            userId = myUserId, //FIX THIS TO BE OWN USERID
                            post = post
                        )
                        GlobalScope.launch {
                            likePost(payload)
                            withContext(Dispatchers.Main) {
                                if (payload.userId in post.userLikedList) {
                                    likeCount = likeCount - 1
                                    post.userLikedList.remove(payload.userId)
                                } else {
                                    likeCount = likeCount + 1
                                    post.userLikedList.add(payload.userId)
                                }

                            }
                        }
                    },
                ) {
                    if (myUserId in post.userLikedList) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "liked button",
                        )
                    }
                    else{
                        Icon(
                            Icons.Outlined.FavoriteBorder,
                            contentDescription = "Not yet liked button",
                        )
                    }

                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(likeCount.toString())
                }
                ElevatedButton(
                    onClick = {
                        isCommentModalOpen = true
                    },
                ) {
                    Icon(
                        Icons.Outlined.ChatBubble,
                        contentDescription = "Comment Icon"
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(commentCount.toString())
                }
                ElevatedButton(
                    onClick = {
                        GlobalScope.launch {
                            bananaPost(post)
                            bananas += 1
                        }
                    },
                ) {
                    Text("🍌")
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(bananas.toString())
                }
            }

        if (isCommentModalOpen) {
            CommentModal(post, navController, onDismiss = { isCommentModalOpen = false })
        }
        }
    }
}
