package com.example.poopoo.components

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import com.example.poopoo.api.getPosts
import bemonke.models.*
import coil.compose.AsyncImage
import com.example.poopoo.PreferencesManager
import com.example.poopoo.api.getUserPosts
import com.example.poopoo.navigation.PostImage
import com.example.poopoo.navigation.ProfileImage
import com.example.poopoo.navigation.Screen
import com.example.poopoo.ui.theme.POOPOOTheme

@Composable
fun ManaMeter() {
    var level by remember { mutableStateOf(5) }
    var amount by remember { mutableStateOf(30) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Mana Level: $level")
        Text("Amount Achieved: $amount")
        Spacer(modifier = Modifier.height(16.dp))
        ManaMeterVisualizer(level, amount)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { if (level > 1) level-- }) {
                Icon(Icons.Default.ArrowCircleLeft, contentDescription = null)
                Text("Decrease Level")
            }

            Button(onClick = { if (level < 10) level++ }) {
                Text("Increase Level")
                Icon(Icons.Default.ArrowCircleLeft, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
}

@Composable
fun ManaMeterVisualizer(level: Int, amount: Int) {
    val meterHeight = with(LocalDensity.current) { 24.dp.toPx() }
    val meterWidth = with(LocalDensity.current) { 300.dp.toPx() }
    val cornerRadius = 4.0f

    Canvas(
        modifier = Modifier
            .background(Color.Gray.copy(alpha = 0.2f))
            .size(meterWidth.dp, meterHeight.dp)
    ) {
        val rect = Rect(0f, 0f, meterWidth, meterHeight)
        drawRoundRect(
            color = Color.Gray.copy(alpha = 0.8f),
            size = rect.size,
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )

        val progressWidth = (amount.toFloat() / level) * meterWidth
        drawRoundRect(
            color = Color.Blue,
            size = rect.size.copy(width = progressWidth),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )
    }
}

@Preview
@Composable
fun BananaMeterPreview() {
    POOPOOTheme {
        ManaMeter()
    }
}