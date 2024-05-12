package com.example.poopoo.pages


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.example.poopoo.api.getPosts
import bemonke.models.*
import coil.compose.AsyncImage
import com.example.poopoo.PreferencesManager
import com.example.poopoo.api.addFriend
import com.example.poopoo.api.getUserPosts
import com.example.poopoo.components.DeletePostDialog
import com.example.poopoo.components.PostComponent
import com.example.poopoo.navigation.PostImage
import com.example.poopoo.navigation.ProfileImage
import com.example.poopoo.navigation.Screen
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    navController: NavController,
//    changeRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    r: Boolean = false
) {
    var doRefresh = r
    val coroutineScope = rememberCoroutineScope()
    var (loadResult, setLoadResult) = remember { mutableStateOf<String?>(null) }
//    var realPostData by remember { mutableStateOf<List<Post>>(emptyList()) }
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val myUserId = preferencesManager.getData("userId","")
    val realPostData = remember { mutableStateOf<List<Post>>(
        Json.decodeFromString(preferencesManager.getData("realPostData","[]"))) }

    suspend fun refresh() {
        doRefresh = false
        // Fetch the data
        val posts = getPosts(myUserId)

        // Update realPostData with the fetched data
        realPostData.value = posts

        preferencesManager.saveData("realPostData",Json.encodeToString(posts))
    }

    DisposableEffect(Unit) {
        val apiCallJob = coroutineScope.launch {
            while (true) {
                refresh()
               delay(3000)
            }
        }
        onDispose {
            apiCallJob.cancel()
        }
    }

    if (!realPostData.value.isNotEmpty() || doRefresh) {
        Log.d("!","REFRESH")
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    refresh()
                }
            }
        }
    }

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    AsyncImage(
//                model = "https://i.imgur.com/RtSONcJ.png", // LIGHT
                        model = "https://imgur.com/HdBxunZ.png", // DARK
                        contentDescription = "logo"
                    )
                },
                modifier = Modifier
                    .height(56.dp)
                    .clickable {
                        GlobalScope.launch {
                            refresh()
                        }
                    },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(modifier = modifier.fillMaxSize()) {
                PostList(
//                    posts = mockPostData,
                    posts = realPostData.value,
                    navController = navController,
                    lazyListState = rememberLazyListState()
                )
            }
        }
    }
}




@Composable
fun PostList(
    posts: List<Post>,
    lazyListState: LazyListState,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier, state = lazyListState) {
        item {
            // TOP ITEM
        }
        items(items = posts,
            key = { it.time }
        ) { post ->
            PostComponent(
                post = post,
                navController = navController
            )
        }
    }
}

@Composable
fun UserPostsScreen(
    userId: String,
    selectedIndex: Int,
    navController: NavController
) {
    Log.d("PostList", "Overall index of post: $selectedIndex")
    val posts = remember { mutableStateOf<List<Post>>(emptyList()) }
    val lazyListState = rememberLazyListState()
    var postsLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        posts.value = getUserPosts(userId)
        postsLoaded = true
    }

    LaunchedEffect(postsLoaded) {
        if (postsLoaded) {
            lazyListState.scrollToItem(index = selectedIndex + 1)
        }
    }

    PostList(posts = posts.value, lazyListState = lazyListState, navController = navController)
}

