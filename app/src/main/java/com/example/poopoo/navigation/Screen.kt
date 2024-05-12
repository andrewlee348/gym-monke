package com.example.poopoo.navigation

sealed class Screen (val route: String) {
    object HomeScreen: Screen(route = "home_screen")
    object ExploreScreen: Screen(route = "explore_screen")
    object ProfileScreen: Screen(route = "profile_screen")
    object CameraScreen: Screen(route = "camera_screen")
    object SettingsScreen: Screen(route = "settings_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach{ arg ->
                append("/$arg")
            }
        }
    }
}