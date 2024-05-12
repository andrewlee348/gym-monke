package com.example.poopoo


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.poopoo.api.SERVER_URL
import com.example.poopoo.navigation.Screen
import com.example.poopoo.navigation.SetupNavGraph
import com.example.poopoo.pages.SignUpPage

@Composable
fun BeMonkeApp(
    closeDetailScreen: () -> Unit = {},
    navigateToDetail: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    SERVER_URL = preferencesManager.getData("serverUrl", "http://10.0.2.2:8080")
    Surface(tonalElevation = 5.dp) {
        val navController = rememberNavController()
        val preferencesManager = remember { PreferencesManager(context) }
        val isLoggedIn = remember { mutableStateOf(preferencesManager.getData("isLoggedIn", "false")) }

        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn.value == "false") "signup" else "home"
        ) {
            composable("signup") {
                SignUpPage { navController.navigate("home") }
            }
            composable("home") {
                BeMonkeAppContent{ navController.navigate("signup") }
            }
        }
    }
}


@Composable
fun BeMonkeAppContent(
    navigateToSignUp: () -> Unit
) {
    // Navigation Controller
    val navController: NavHostController = rememberNavController()
    // Nav bar
    val selectedDestination = remember { mutableStateOf(Screen.HomeScreen.route) }
    // Snackbar messages
    val snackbarHostState = remember { SnackbarHostState() }
    fun getSnackbarHostState(): SnackbarHostState {return snackbarHostState}
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.fillMaxWidth(),
                //            .align(Alignment.BottomCenter)
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                TOP_LEVEL_DESTINATIONS.forEach { replyDestination ->
                    NavigationBarItem(
                        selected = selectedDestination.value == replyDestination.screen.route,
                        onClick = {
//                            selectedDestination.value = navController.currentDestination?.route + ""

                            //                            navController.navigate(replyDestination.screen.route)
                            navController.navigate(replyDestination.screen.route) {
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }

                            selectedDestination.value = replyDestination.screen.route
                        },
                        icon = {
                            Icon(
                                imageVector = replyDestination.selectedIcon,
                                contentDescription = replyDestination.screen.route
                            )
                        }
                    )
                }
            }
        }
    ) {
        paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            SetupNavGraph(navController = navController, navigateToSignUp = navigateToSignUp)
        }
    }
}



object Routes {
    const val SIGN_UP = "SignUp"
    const val HOME = "Home"
    const val EXPLORE = "Explore"
    const val CAMERA = "Camera"
    const val DM = "DM"
    const val PROFILE = "Profile"
    const val SETTINGS = "Settings"
}
//
data class TopLevelDestination(
    val screen: Screen,
    val selectedIcon: ImageVector,
    val unselectedIcon: Any,
//    val unselectedIcon: ImageVector,
//    val iconTextId: Int
)

val TOP_LEVEL_DESTINATIONS = listOf(
    TopLevelDestination(
        screen = Screen.HomeScreen,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
//        unselectedIcon = Icons.Default.Inbox,
//        iconTextId = R.string.tab_inbox
    ),
    TopLevelDestination(
        screen = Screen.ExploreScreen,
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search,
//        unselectedIcon = Icons.Default.Search,
//        iconTextId = R.string.tab_article
    ),
    TopLevelDestination(
        screen = Screen.CameraScreen,
        selectedIcon = Icons.Filled.AddCircle,
        unselectedIcon = Icons.Outlined.AddCircle,
//        iconTextId = R.string.tab_inbox
    ),
    TopLevelDestination(
        screen = Screen.ProfileScreen,
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle,
//        iconTextId = R.string.tab_article
    ),
    TopLevelDestination(
        screen = Screen.SettingsScreen,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Default.Settings,
//        iconTextId = R.string.tab_article
    )
)