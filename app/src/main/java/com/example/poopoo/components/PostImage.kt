package com.example.poopoo.navigation
import androidx.compose.ui.layout.ContentScale
import SettingsPage
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import bemonke.models.Post
import coil.compose.AsyncImage
import com.example.poopoo.CameraModule
import com.example.poopoo.pages.ProfilePage
import com.example.poopoo.pages.CameraPage
import com.example.poopoo.pages.ExplorePage
import com.example.poopoo.pages.HomePage
import kotlinx.coroutines.delay
import kotlin.math.abs

val yellowColorMatrix = floatArrayOf(
    1f, 1f, 0f, 0f, 0f,  // Red channel (1 * R + 1 * G + 0 * B)
    0f, 1f, 0f, 0f, 0f,  // Green channel (0 * R + 1 * G + 0 * B)
    0f, 0f, 0f, 0f, 0f,  // Blue channel (0 * R + 0 * G + 0 * B)
    0f, 0f, 0f, 1f, 0f   // Alpha channel (no change)
)
@Composable
fun PostImage(post: Post, bananas: Int) {
    Box(
        modifier = Modifier
            .size(400.dp)
            .clip(RoundedCornerShape(16.dp))
            .fillMaxSize()
            .background(
                    Color.Yellow
            )
    ) {
        AsyncImage(
            model = post.imageUrl,
            contentDescription = "image post",
            contentScale = ContentScale.Crop, // Use ContentScale.Fit
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
        )
        EmojiFrame(post, bananas)
    }
}

@Composable
fun EmojiFrame(post: Post, bananas: Int) {

    val scrollState = rememberLazyListState()
    val scrollState2 = rememberLazyListState()

    LaunchedEffect(key1 = Unit) {
        while (true) {
            scrollState.animateScrollBy(
                value = (100*bananas/2).toFloat(),
                animationSpec = tween(durationMillis = 5000)
            )
            scrollState.animateScrollBy(
                value = -(100*bananas/2).toFloat(),
                animationSpec = tween(durationMillis = 5000)
            )
        }
    }
    LaunchedEffect(key1 = Unit) {
        scrollState2.scrollToItem(bananas/2)
        while (true) {
            scrollState2.animateScrollBy(
                value = -(100*bananas/2).toFloat(),
                animationSpec = tween(durationMillis = 5000)
            )
            scrollState2.animateScrollBy(
                value = (100*bananas/2).toFloat(),
                animationSpec = tween(durationMillis = 5000)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Emoji frame
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
                state = scrollState
            ) {
                items(bananas/2) {
                    Emoji(post.time + it.toString() + "sugon deez nuts andrew")
                }
            }

            // Middle row with image

            // Bottom row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
                state = scrollState2
            ) {
                items((bananas+1)/2) {
                    Emoji(post.time + it.toString())
                }
            }
        }
    }
}

val MONKE_BIBLE = arrayOf(
    "https://cdn.discordapp.com/attachments/529783388295528470/1175436621773029437/image0.png?ex=656b39a9&is=6558c4a9&hm=2a2e56365ab5bf893ee1eacddfc1c59a4b42291bf32de6e0941d0e5ce86614f3&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175436622087606333/image1.png?ex=656b39a9&is=6558c4a9&hm=d81464578c1da0698de71d9c8440bc6fb2913dfca87517060c0d46c669bf68f4&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175436622431522876/image2.png?ex=656b39a9&is=6558c4a9&hm=cec35fdb07e13a1d7620cebeca58c89794151bd2161ad7463fc08ce6d899a37f&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175436622733525003/image3.png?ex=656b39a9&is=6558c4a9&hm=b729f55d2a16749bf56c83155489323f15e947a49eac1af969295aa3f1738507&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175436623115194439/image4.png?ex=656b39a9&is=6558c4a9&hm=78777dd0563803522d210585a7703514158968ea207d0a4623c0f9b4166e1209&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175436623383642223/image5.png?ex=656b39a9&is=6558c4a9&hm=c3cb5a82e00e3cf9a2095ed501d90fca6c7294497ab40d46a0dfaeea2605f7f6&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175436623933100042/image6.png?ex=656b39a9&is=6558c4a9&hm=e931f5f1e87ea90d16415aed596c2e3f36a73ced4d61f55cea0612826cf51426&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175436624339931176/image7.png?ex=656b39a9&is=6558c4a9&hm=810d108482f69775e4c37059518ba2864bcbcee46dc1120c08c416ecf500a566&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175436624746795038/image8.png?ex=656b39a9&is=6558c4a9&hm=315d6082582ee48ff83a8eb44d235cb18e2f1469009139b7d34cbc4f6590b5d2&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175436625245900841/image9.png?ex=656b39aa&is=6558c4aa&hm=1463f5877bb6630ceec732c825b61a186ab83e8f1f696efca0904da37fefaab8&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175436810239877140/image1.png?ex=656b39d6&is=6558c4d6&hm=07d772d9612ae59af5c678ec66dbb6b7e5b1dd797ca9c8c7d1dec7898ffa7e02&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175436810478964746/image2.png?ex=656b39d6&is=6558c4d6&hm=d82c976448dd58c682c93f6cd558ae0a618cecf7ae7ab46b6213bf79ff9175ef&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175436810713825320/image3.png?ex=656b39d6&is=6558c4d6&hm=264604f10773651f3457b19ec95fef82ed4eba48c449e1fc6ae39b350af93014&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175436810957103104/image4.png?ex=656b39d6&is=6558c4d6&hm=32f8413a0bb1e8d9f3ab3a9e10c3c0519625409df9189185f136c9d1fd21f027&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175436811204579328/image5.png?ex=656b39d6&is=6558c4d6&hm=a8a095e83f805178353d75747bd2f2d7b4a5546ef78a10ac9e6f3bb51e269d48&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175436812278308954/image6.png?ex=656b39d6&is=6558c4d6&hm=628556efa7533da3b61abc5b99947554bc6eca6628a1bf94e9e2fd84f4f58b61&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175438061526589510/image0.png?ex=656b3b00&is=6558c600&hm=7a3ac86a195885d9ea1f6c22c8c9058125a5c3858330159a07a938d0ece37708&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175438061950222366/image1.png?ex=656b3b00&is=6558c600&hm=226d817ff87148211b6f60976f9b64933e75cf29eaa99745134c36cf45d13076&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175438062214459422/image2.png?ex=656b3b00&is=6558c600&hm=c3e8e1cd4520a1c4993e78180ec490baadf1aee12f156ba289fed0182e50ce44&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175438062495473674/image3.png?ex=656b3b00&is=6558c600&hm=7d7c6a4c342a3d9f1f9544e64ac011f15cbd25de176d546e87ebe0136cebb89c&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175438062763917322/image4.png?ex=656b3b00&is=6558c600&hm=aa872460f25e4439d2cddf25a9efc634a04a5e71b6f316858c97220e55ed3a3f&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175438063036543027/image5.png?ex=656b3b00&is=6558c600&hm=fd0c44d605f06e753847dac83e4e7d0a288575b2b2847833b75dc38e2e65b435&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175438063342731314/image6.png?ex=656b3b00&is=6558c600&hm=664e7473b44b852189be49be851ea5c42201321df341f3cda280be56035772b9&",
    "https://cdn.discordapp.com/attachments/529783388295528470/1175438063611162755/image7.png?ex=656b3b00&is=6558c600&hm=49779b19c66375ff213540041054daac566cda346409c243fcd070997277130a&"
)

@Composable
fun Emoji(hash: String) {
    val rng = java.util.Random(hash.hashCode().toLong())
    val index = abs(rng.nextInt()) % MONKE_BIBLE.size
    val imageUrl = MONKE_BIBLE[index]
    val rotationAngle = rng.nextFloat() * 160 - 80

    AsyncImage(
        model = imageUrl,
        contentDescription = "monke",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .padding(4.dp)
            .size(50.dp)
            .rotate(rotationAngle) // Apply the rotation
    )
}