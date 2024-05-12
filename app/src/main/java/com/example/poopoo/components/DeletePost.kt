package com.example.poopoo.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bemonke.models.DBPost
import bemonke.models.Post
import com.example.poopoo.api.bananaPost
import com.example.poopoo.api.deletePost
import com.example.poopoo.api.editPostCaption
import com.example.poopoo.api.postPost
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun PostEditMenu(post: Post) {
    var expanded by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    val openEditDialog = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
//            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
    ) {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = { openEditDialog.value = true },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = null
                    )
                })
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = { openDialog.value = true },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = null
                    )
                })
        }
    }

    if (openDialog.value) {
        DeletePostDialog(openDialog = openDialog, post = post)
    }
    if (openEditDialog.value) {
        EditPostDialog(openDialog = openEditDialog, post = post)
    }
}

@Composable
fun DeletePostDialog(openDialog: MutableState<Boolean>, post: Post) {
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
            openDialog.value = false
        },
        title = {
            Text(text = "Delete Post")
        },
        text = {
            Text(
                "Are you sure you want to delete this post?"
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    openDialog.value = false
                    GlobalScope.launch {
                        deletePost(post)
                    }
                }
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    openDialog.value = false
                }
            ) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun EditPostDialog(openDialog: MutableState<Boolean>, post: Post) {
    var newCaption by remember {mutableStateOf("")}
    newCaption = post.caption
    val trailingIconView = @Composable {
        IconButton(
            onClick = {
                newCaption = ""
            },
        ) {
            Icon(
                Icons.Default.Clear,
                contentDescription = "x button",
                tint = Color.Black
            )
        }
    }
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
            openDialog.value = false
        },
        title = {
            Text(text = "Edit Caption")
        },
        text = {
            TextField(
                value = newCaption,
                onValueChange = {
                    newCaption = it
                },
                placeholder = { Text("Caption") },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
//                    .weight(1f)
                    .padding(horizontal = 6.dp),

                //x button only shown when there is input text
                trailingIcon = if (newCaption.isNotBlank()) trailingIconView else null,
            )
        },

        confirmButton = {
            TextButton(
                onClick = {
                    openDialog.value = false
                    val newPost = DBPost(
                        userId = post.userId,
                        username = post.username,
                        time = post.time,
                        caption = newCaption,
                        imageUrl = post.imageUrl,
                        userLikedList = post.userLikedList,
                        banana = post.banana,
                        comments = post.comments
                    )
                    GlobalScope.launch {
                        editPostCaption(newPost)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    openDialog.value = false
                }
            ) {
                Text("Cancel")
            }
        }
    )
}
