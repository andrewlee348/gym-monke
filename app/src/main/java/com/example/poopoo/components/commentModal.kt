package com.example.poopoo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.poopoo.PreferencesManager
import com.example.poopoo.navigation.ProfileImage
import com.example.poopoo.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import bemonke.models.Post
import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Clear
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import bemonke.models.ReturnCommentsPayload
import bemonke.models.UpdateCommentPayload
import com.example.poopoo.api.addComment
import com.example.poopoo.api.getPosts
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.*
import androidx.compose.material.icons.outlined.*
import bemonke.models.*
import com.example.poopoo.api.*

@Composable
fun CommentModal(
    post: Post,
    navController: NavController,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
){
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val userId = remember { mutableStateOf(preferencesManager.getData("userId", "")) }
    var createCommentText by remember {mutableStateOf("")}
    var comments = remember { mutableStateOf<List<CommentData>>(emptyList()) }

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    // Fetch the data
                    val dataPayload = getCommentsPayload(post.userId,post.time)
                    val data = getComments(dataPayload)

                    // Update realPostData with the fetched data
                    comments.value = data.comments
                    Log.d("!", Json.encodeToString(comments))
                } catch (e: Exception) {
                    Log.d("test", e.message + "")
                    // Handle exception
                }
            }
        }
    }
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        // Other elements in the row
                        // ...

                        // IconButton
                        IconButton(
                            onClick = {
                                onDismiss()
                            },
                        ) {
                            Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close")
                        }
                    }


                    Text(
                        text = "Comments",
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                            .padding(horizontal = 16.dp)
                    ) {
                        items(items = comments.value) { comment ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate(route = Screen.ProfileScreen.route + "?userId=" + post.userId)
                                    },
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Column(
                                    modifier = Modifier.padding(top = 15.dp)
                                ) {
                                    var postCopy = post.copy(profilePicUrl = comment.profilePicUrl)
                                    Log.d("!", postCopy.profilePicUrl)
                                    Log.d("!", comment.profilePicUrl)
                                    ProfileImage(postCopy)
                                }
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 12.dp, vertical = 18.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = comment.commentUsername,
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                    Text(
                                        text = comment.commentBody,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (comment.commentUserId == preferencesManager.getData("userId", "")) {
                                    CommentEditMenu(post,comment)
                                }
                            }
                        }
                    }


                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                val payload = UpdateCommentPayload(
                                    postUserId = post.userId,
                                    commentUserId = preferencesManager.getData("userId", ""),
                                    postTime = post.time,
                                    commentTime = LocalDateTime.now().format(formatter),
                                    commentBody = createCommentText
                                )
                                GlobalScope.launch {
                                    addComment(payload)
                                }
                                navController.navigate(Screen.HomeScreen.route)
                            }
                        ) {
                            Text("Post Comment")
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(vertical = 15.dp) // Add horizontal padding here
                            .padding(horizontal = 10.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        //x icon on the right to clear text
                        val trailingIconView = @Composable {
                            IconButton(
                                onClick = {
                                    createCommentText = ""
                                },
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "x button",
                                    tint = Color.Black
                                )
                            }
                        }
                        //write a comment
                        TextField(
                            value = createCommentText,
                            onValueChange = {
                                createCommentText = it
                            },
                            placeholder = { Text("Comment: ") },
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 6.dp),

                            //x button only shown when there is input text
                            trailingIcon = if (createCommentText.isNotBlank()) trailingIconView else null,
                        )
                    }
                }
            }
        }
}

