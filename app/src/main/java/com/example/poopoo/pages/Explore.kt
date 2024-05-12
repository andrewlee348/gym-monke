package com.example.poopoo.pages

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bemonke.models.UpdateFriendPayload
import bemonke.models.User
import coil.compose.AsyncImage
import com.example.poopoo.PreferencesManager
import com.example.poopoo.api.*
import com.example.poopoo.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ExplorePage(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }

    Text(text = "Explore")
    val searchFieldText = remember { mutableStateOf("") }
    val filteredUserData = remember { mutableStateOf<List<User>>(emptyList()) }
    val currUserData = remember { mutableStateOf<List<User>>(emptyList()) }
    val allUserData = remember { mutableStateOf<List<User>>(emptyList()) }
    val friendUserData = remember { mutableStateOf<List<User>>(emptyList()) }
    var displayOnlyFriends by remember { mutableStateOf(true) }

    //get user list
    val coroutineScope = rememberCoroutineScope()


    //get all friends first
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    friendUserData.value = getUserFriends(preferencesManager.getData("userId",""))
                    filteredUserData.value = friendUserData.value
                } catch (e: Exception) {
                    Log.d("error getting friendUserData", e.message.orEmpty())
                }
                try {
                    allUserData.value = getAllUsers()
                } catch (e: Exception) {
                    Log.d("error getting allUserData", e.message.orEmpty())
                }
            }
        }
    }

    //once the search bar changes, get search results
    LaunchedEffect(searchFieldText.value,displayOnlyFriends) {
        try {
            filteredUserData.value = filterList(currUserData.value,searchFieldText.value)
        } catch (e: Exception) {
            Log.d("error getting filterList", e.message.orEmpty())
        }
    }
    //x icon on the right to clear text
    val trailingIconView = @Composable {
        IconButton(
            onClick = {
                searchFieldText.value = ""
            },
        ) {
            Icon(
                Icons.Default.Clear,
                contentDescription = "x button",
                tint = Color.Black
            )
        }
    }
    Scaffold(
        //topBar is the search bar
        topBar = {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 15.dp) // Add horizontal padding here
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextField(
                        value = searchFieldText.value,
                        onValueChange = {
                            searchFieldText.value = it },
                        placeholder = { Text("Search for Friends") },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),

                        //search icon on the left
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.Black // Set the icon color to black
                            )
                        },
                        //x button only shown when there is input text
                        trailingIcon = if (searchFieldText.value.isNotBlank()) trailingIconView else null,
                    )
                }
                Row (
                    modifier = Modifier

                        .padding(vertical = 15.dp) // Add horizontal padding here
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (displayOnlyFriends) {
                        ElevatedButton(onClick = {
                            currUserData.value = allUserData.value
                            displayOnlyFriends = false
                        }) {Text("Search All Monkes")}
                    }
                    else {
                        ElevatedButton(onClick = {
                            currUserData.value = friendUserData.value
                            displayOnlyFriends = true
                        }) {Text("Search Friend Monkes")}
                    }
                }
            }
        },
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(modifier = modifier.fillMaxSize()) {
                UserList(
                    users = filteredUserData.value,
                    navController = navController,
                    lazyListState = rememberLazyListState()
                )
            }
        }
    }
}

@Composable
fun UserComponent(
    user: User,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =  modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable {
                    navController.navigate(route= Screen.ProfileScreen.route+"?userId="+user.userId)
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    model = user.profilePicUrl,
                    contentDescription = "Profile Picture",
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 15.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = user.username,
                        style = MaterialTheme.typography.titleMedium, // Change the style to a larger font size
                    )
                }
            }
        }
    }
}

@Composable
fun UserList(
    users: List<User>,
    navController: NavController,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier, state = lazyListState) {
        item {
            // TOP ITEM
        }
        items(items = users,
//            key = { it.postId }
        ) { user ->
            UserComponent(
                user = user,
                navController = navController
            )
        }
    }
}
