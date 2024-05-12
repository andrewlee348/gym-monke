package com.example.poopoo.navigation

import SettingsPage
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import bemonke.models.Post
import bemonke.models.User
import coil.compose.AsyncImage
import com.example.poopoo.CameraModule
import com.example.poopoo.pages.ProfilePage
import com.example.poopoo.pages.CameraPage
import com.example.poopoo.pages.ExplorePage
import com.example.poopoo.pages.HomePage

@Composable
fun ProfileImage(post : Post) {
    val rainbowColorsBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFF9575CD),
                Color(0xFFBA68C8),
                Color(0xFFE57373),
                Color(0xFFFFB74D),
                Color(0xFFFFF176),
                Color(0xFFAED581),
                Color(0xFF4DD0E1),
                Color(0xFF9575CD)
            )
        )
    }
    val borderColour = rainbowColorsBrush

    AsyncImage(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
//            .border(
//                BorderStroke(2.dp, borderColour),
//                CircleShape
//            )
        ,
        model = post.profilePicUrl,
        contentDescription = "jacked guy pic",
    )
}