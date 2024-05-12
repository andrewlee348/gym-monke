package com.example.poopoo.pages

import android.app.Activity
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bemonke.models.User
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.poopoo.PreferencesManager
import com.example.poopoo.api.createUser
import com.example.poopoo.api.uploadImageToImgur
import com.example.poopoo.navigation.yellowColorMatrix
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileDescriptor
import kotlin.random.Random

@Composable
fun SignUpPage(navigateToHome: () -> Unit) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }

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

    var usernameField by remember { mutableStateOf("") }

    // Google OAuth Code
    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully
            val username = account.displayName
            val profilePicUrl = account.photoUrl.toString()
            val userId = account.id.toString()
            preferencesManager.saveData("userId", userId)
            if (username != null) {
                preferencesManager.saveData("username", username)
            }
            preferencesManager.saveData("profilePicUrl", profilePicUrl)
            val user = username?.let {
                User(
                    userId = userId,
                    username = it,
                    profilePicUrl = profilePicUrl,
                    friendList = mutableListOf()
                )
            }
            GlobalScope.launch {
                if (user != null) {
                    createUser(user)
                }
            }
            preferencesManager.saveData("isLoggedIn", "true")
            navigateToHome()

        } catch (e: ApiException) {
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.statusCode)
        }
    }

    val googleSignInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, googleSignInOptions) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }
    }



    Scaffold(
        topBar = {
            AsyncImage(
//                model = "https://i.imgur.com/RtSONcJ.png", // LIGHT
                model = "https://imgur.com/HdBxunZ.png", // DARK
                contentDescription = "logo"
            )
        }
    ) {
            innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = "Welcome to BeMonke!", fontSize = 30.sp)
            Text(text = "Login with Google", fontSize = 30.sp)
            Spacer(modifier = Modifier.height(16.dp))
//            OutlinedTextField(
//                value = usernameField,
//                onValueChange = { usernameField = it },
//                label = { Text("Username") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            IconButton(
//                onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
//                modifier = Modifier
//                    .size(65.dp)
//                    .padding(8.dp)
//                    .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary)),
//            ) {
//                Icon(
//                    Icons.Default.Image,
//                    contentDescription = "gallery",
//                    tint = MaterialTheme.colorScheme.primary
//                )
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//            Button(
//                enabled = buttonEnabled,
//                onClick = {
//                    val userId = Random.nextInt(1000, 10000).toString()
//                    preferencesManager.saveData("userId", userId)
//                    preferencesManager.saveData("username", usernameField)
//                    preferencesManager.saveData("profilePicUrl", profilePic)
//                    val user = User(
//                        userId = userId,
//                        username = usernameField,
//                        profilePicUrl = profilePic,
//                        friendList = mutableListOf()
//                    )
//                    GlobalScope.launch {
//                        createUser(user)
//                    }
//                    preferencesManager.saveData("isLoggedIn", "true")
//                    navigateToHome()
//                },
//                content = {
//                    Text("Create Account")
//                }
//            )
            Button(onClick = {
                googleSignInClient.signOut().addOnCompleteListener {
                    // After signing out, launch the sign-in intent
                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
                }
            }) {
                Text("Sign in with Google")
            }
        }
    }
}