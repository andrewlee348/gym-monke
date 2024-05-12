package com.example.poopoo.pages

//import com.dheeraj.camera_example.ui.theme.Camera_exampleTheme

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.poopoo.api.ApiCreatePost
import java.io.File
import java.io.FileDescriptor
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.poopoo.CameraModule
import com.example.poopoo.PreferencesManager
import com.example.poopoo.api.NewImgurApi
import com.example.poopoo.navigation.Screen

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CameraPage(
    navController: NavController
) {

    // Camera Module
    val CAMERA = CameraModule()
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    capturedImageUri = CAMERA("getCapturedImageUri")() as Uri

    val launchCamera = CAMERA("launchCamera")
    // photo picker
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            if (it != null) {
                it.path?.let { it1 -> Log.d("test", it1) }
                capturedImageUri = it
            }
        }
    )
    val context = LocalContext.current
    var caption by remember {mutableStateOf("")}

    val preferencesManager = remember { PreferencesManager(context) }
    val userId = remember { mutableStateOf(preferencesManager.getData("userId", "")) }
    val username = remember { mutableStateOf(preferencesManager.getData("username", "")) }
    val profilePicUrl = remember { mutableStateOf(preferencesManager.getData("profilePicUrl", "")) }


    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "back button",
                        tint = Color.Black
                    )
                }
            }
        },

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
                            caption = ""
                        },
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "x button",
                            tint = Color.Black
                        )
                    }
                }

                TextField(
                    value = caption,
                    onValueChange = {
                        caption = it
                    },
                    placeholder = { Text("Caption: ") },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp),

                    //x button only shown when there is input text
                    trailingIcon = if (caption.isNotBlank()) trailingIconView else null,
                )
            }

            Card() {
                if (capturedImageUri!!.path?.isNotEmpty() == true) {
                    Image(
                        modifier = Modifier,
                        painter = rememberImagePainter(capturedImageUri),
                        contentDescription = null
                    )
                } else {
                    Text("Take a photo!")
                }
            }

        }

        Box(
            modifier = Modifier
                .fillMaxHeight(),
            contentAlignment = Alignment.TopCenter
        ) {

        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .height(100.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
                ) {

                ElevatedButton(onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier= Modifier.size(65.dp),
                    shape = CircleShape,
                    border= BorderStroke(8.dp, MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.Blue)
                ) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = "gallery",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                OutlinedButton(onClick = { launchCamera() },
                    modifier= Modifier.size(65.dp),
                    shape = CircleShape,
                    border= BorderStroke(8.dp, MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.Blue)
                ) {
                    Icon(
                        Icons.Default.PhotoCamera,
                        contentDescription = "camera",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                ElevatedButton(
                    enabled = capturedImageUri!!.path?.isNotEmpty() == true,
                    modifier= Modifier.size(65.dp),
                    shape = CircleShape,
                    border= BorderStroke(8.dp, MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.Blue),
                    onClick = {
                        NewImgurApi()


                        val c = context.contentResolver
                        val parcelFileDescriptor = c.openFileDescriptor(capturedImageUri!!, "r")
                        val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
                        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                        parcelFileDescriptor.close()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val time = LocalDateTime.now().format(formatter)

                        //                        scope.launch {BeMonkeAppContent.getSnackbarHostState().showSnackbar("Uploading Post")}
                        ApiCreatePost(userId.value, username.value, time, image, caption = caption)
                        navController.navigate(Screen.HomeScreen.route)
                    }
                ) {
                    Icon(
                        Icons.Filled.Send,
                        contentDescription = "content description",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}
