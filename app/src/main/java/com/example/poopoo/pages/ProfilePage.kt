package com.example.poopoo.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import bemonke.models.Post
import bemonke.models.UpdateFriendPayload
import bemonke.models.User
import coil.compose.AsyncImage
import com.example.poopoo.PreferencesManager
import com.example.poopoo.api.getUserData
import com.example.poopoo.api.getUserFriends
import com.example.poopoo.api.getUserPosts
import com.example.poopoo.api.*
import com.example.poopoo.ui.theme.POOPOOTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

@Composable
fun ProfilePage(navController: NavController, inputUserId : String? = null) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }

    val userId = inputUserId?:preferencesManager.getData("userId", "")

    val isSelf = userId == preferencesManager.getData("userId", "")
    var isFriend by remember { mutableStateOf(false) }

    //get userData
    val userData = remember { mutableStateOf<User?>(null) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val users = getUserData(userId)
                val friendsList = getUserFriends(preferencesManager.getData("userId", ""))
                Log.d("test1", friendsList.toString())
                for (idx in 0..friendsList.size-1) {
                    if (friendsList[idx].userId == userId) {
                        isFriend = true
                        break
                    }
                }

                // Update realUserData with the fetched data
                userData.value = users
                Log.d("test", userData.value.toString())
            }
        }
    }

    //get userPosts
    val userPosts = remember { mutableStateOf<List<Post>>(emptyList()) }
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                userPosts.value = getUserPosts(userId)
                Log.d("test", userPosts.value.toString())
            }
        }
    }

    val onPictureClick: (Int, String) -> Unit = { index, userId ->
        navController.navigate("userPosts/$userId/$index")
    }
    Text("")
    if (userData.value != null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture
            item{
                AsyncImage(
                    modifier = Modifier.size(120.dp).clip(CircleShape),
                    model = userData.value?.profilePicUrl,
                    contentDescription = "jacked guy pic",
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Name
                Text(text = userData.value?.username+ "", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))

                if (!isSelf) {
                    if (isFriend) {
                        ElevatedButton(onClick = {
                            val payload = UpdateFriendPayload(
                                userId = preferencesManager.getData("userId", ""),
                                friendId = userId
                            )
                            GlobalScope.launch {
                                removeFriend(payload)
                            }
                            isFriend = false
                        }) {Text("Remove Friend")}
                    }
                    else {
                        ElevatedButton(onClick = {
                            val payload = UpdateFriendPayload(
                                userId = preferencesManager.getData("userId", ""),
                                friendId = userId
                            )
                            GlobalScope.launch {
                                addFriend(payload)
                            }
                            isFriend = true
                        }) {Text("Add Friend")}
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

            // userId
                Text(text = userId, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))

                // Daily posts
                Text(text = "Your Daily Posts", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            val chunkSize = 3
            val chunks = userPosts.value.chunked(chunkSize)
            itemsIndexed(chunks) { chunkIndex, chunk ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    chunk.forEachIndexed { indexWithinChunk, post ->
                        // Correctly calculate the overall index
                        val overallIndex = chunkIndex * chunkSize + indexWithinChunk
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(15.dp))
                                .background(Color.LightGray)
                                .clickable { onPictureClick(overallIndex, userId) }
                        ) {
                            AsyncImage(
                                modifier = Modifier.fillMaxSize(),
                                model = post.imageUrl,
                                contentScale = ContentScale.Crop,
                                contentDescription = "Image"
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            item{
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

}