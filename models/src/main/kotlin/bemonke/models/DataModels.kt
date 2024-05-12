package bemonke.models

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val userId: String,
    val username: String,
    var profilePicUrl: String,
    val time: String,
    val caption: String,
    val imageUrl: String,
    val userLikedList: MutableList<String>,
    val banana: Int,
    val comments: MutableList<String>,
)

@Serializable
data class DBPost(
    val userId: String,
    val username: String,
    val time: String,
    val caption: String,
    val imageUrl: String,
    val userLikedList: MutableList<String>,
    val banana: Int,
    val comments: MutableList<String>,
)

@Serializable
data class User(
    val userId: String,
    val username: String,
    val profilePicUrl: String,
    val friendList: MutableList<String>,
)

@Serializable
data class PostPK(
    val userId: String,
    val time: String,
)

@Serializable
data class UpdateFriendPayload(
    val userId: String,
    val friendId: String,
)

@Serializable
data class likePostPayload(
    val userId: String,
    val post: Post,
)

@Serializable
data class UpdateCommentPayload(
    val postUserId: String,
    val commentUserId: String,
    val postTime: String,
    val commentTime: String,
    val commentBody: String,
)

@Serializable
data class CommentData(
    val commentUserId: String,
    val commentTime: String,
    val commentBody: String,
    val profilePicUrl: String,
    val commentUsername: String,
)

@Serializable
data class getCommentsPayload(
    val postUserId: String,
    val postTime: String,
)

@Serializable
data class ReturnCommentsPayload(
    val postUserId: String,
    val postTime: String,
    val comments: MutableList<CommentData>
)

//@Serializable
//data class postComment(
//    val userId: String,
//    val post: Post,
//    val commentText: String
//)

//@Serializable
//data class Comment(
//    val userId: String,
//    val time: String,
//    val commentText: String
//)