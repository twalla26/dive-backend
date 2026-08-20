package com.twalla.divebackend.comment

import com.twalla.divebackend.post.Post
import com.twalla.divebackend.user.User
import com.twalla.divebackend.user.toAuthorResponse

fun CreateCommentRequest.toComment(post: Post, user: User): Comment {
    return Comment(
        content = this.content,
        post = post,
        user = user,
    )
}

fun Comment.toCommentResponse(): CommentResponse {
    return CommentResponse(
        id = this.id,
        content = this.content,
        likeCount = this.likeCount,
        postId = this.post.id,
        author = this.user.toAuthorResponse(),
        createdAt = this.createdAt,
    )
}