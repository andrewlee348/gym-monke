import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.poopoo.PreferencesManager
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import bemonke.models.*
import com.example.poopoo.api.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileDescriptor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(navController: NavController, navigateToSignUp: () -> Unit) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }

    val userId = remember { mutableStateOf(preferencesManager.getData("userId", "")) }
    val username = remember { mutableStateOf(preferencesManager.getData("username", "")) }
    val profilePicUrl = remember { mutableStateOf(preferencesManager.getData("profilePicUrl", "")) }
    val serverUrl = remember { mutableStateOf(preferencesManager.getData("serverUrl", "http://10.0.2.2:8080")) }
    //delete
    val friendId = remember { mutableStateOf(preferencesManager.getData("friendId", "")) }
    //delete

    // profile pic
    var buttonEnabled by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var profilePic = ""

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            if (it != null) {
                it.path?.let { it1 -> Log.d("test", it1) }
                capturedImageUri = it

                val c = context.contentResolver
                val parcelFileDescriptor = c.openFileDescriptor(capturedImageUri!!, "r")
                val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
                val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                uploadImageToImgur(image) { imgurUrl ->
                    profilePic = imgurUrl
                    buttonEnabled = true
                }
            }
        }
    )



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            )
        })
    {
        innerPadding ->
        Column(
            modifier = androidx.compose.ui.Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Card {
//                Text("DEV SERVER URL (use proxy for emulator)")
//                Row() {
//                    RadioButton(
//                        selected = (serverUrl.value == "http://localhost:8080"),
//                        onClick = {
//                            preferencesManager.saveData("serverUrl", "http://localhost:8080")
//                            SERVER_URL = "http://localhost:8080"
//                            serverUrl.value = "http://localhost:8080"
//                        }
//                    )
//                    Text("LocalHost")
//                }
//                Row() {
//                    RadioButton(
//                        selected = (serverUrl.value == "http://10.0.2.2:8080"),
//                        onClick = {
//                            preferencesManager.saveData("serverUrl", "http://10.0.2.2:8080")
//                            SERVER_URL = "http://10.0.2.2:8080"
//                            serverUrl.value = "http://10.0.2.2:8080"
//                        }
//                    )
//                    Text("Proxy")
//                }
//                Row() {
//                    RadioButton(
//                        selected = (serverUrl.value == "http://76.71.43.118:1234"),
//                        onClick = {
//                            preferencesManager.saveData("serverUrl", "http://76.71.43.118:1234")
//                            SERVER_URL = "http://76.71.43.118:1234"
//                            serverUrl.value = "http://76.71.43.118:1234"
//                        }
//                    )
//                    Text("Public")
//                }
//            }
//            TextField(
//                value = userId.value,
//                onValueChange = { userId.value = it
//                    preferencesManager.saveData("userId", userId.value)
//                },
//                placeholder = { Text("Not set ") },
//                label = { Text("userId")},
//                shape = RoundedCornerShape(20.dp),
//                modifier = Modifier.padding(horizontal = 6.dp),
//            )
//            TextField(
//                value = profilePicUrl.value,
//                onValueChange = { profilePicUrl.value = it
//                    preferencesManager.saveData("profilePicUrl", profilePicUrl.value)
//                },
//                placeholder = { Text("Not set ") },
//                label = { Text("profilePicUrl")},
//                shape = RoundedCornerShape(20.dp),
//                modifier = Modifier.padding(horizontal = 6.dp),
//            )

            IconButton(
                onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier
                    .size(65.dp)
                    .padding(8.dp)
                    .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary)),
            ) {
                Icon(
                    Icons.Default.Image,
                    contentDescription = "gallery",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            ElevatedButton(
                enabled = buttonEnabled,
                onClick = {
                GlobalScope.launch {
                    updateProfilePic(userId.value, profilePic)
                }
            }) {Text("Change Profile Pic")}

            Button(onClick = {
                    preferencesManager.saveData("isLoggedIn", "false")
                    navigateToSignUp()
            }) {
                Text("Logout")
            }
        }
    }
}
