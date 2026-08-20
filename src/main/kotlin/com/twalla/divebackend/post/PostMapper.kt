package com.twalla.divebackend.post

import com.twalla.divebackend.user.User
import com.twalla.divebackend.user.toAuthorResponse
import java.time.temporal.ChronoUnit

fun CreatePostRequest.toPost(user: User): Post {
    return Post(
        content = this.content,
        user = user,
    )
}

fun Post.toGetPostSummaryResponse(): GetPostSummaryResponse {
    return GetPostSummaryResponse(
        id = this.id,
        content = this.content.take(100),
        commentCount = this.commentCount,
        likeCount = this.likeCount,
        createdAt = this.createdAt,
        author = this.user.toAuthorResponse(),
    )
}

fun Post.toPostDetailResponse(): PostDetailResponse {
    return PostDetailResponse(
        id = this.id,
        content = this.content,
        viewCount = this.viewCount,
        commentCount = this.commentCount,
        likeCount = this.likeCount,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        author = this.user.toAuthorResponse(),
    )
}

fun Post.toDeletePostResponse(): DeletePostResponse {
    return DeletePostResponse(
        id = this.id,
        deletedAt = requireNotNull(this.deletedAt),
        restorableUntil = requireNotNull(this.deletedAt).plus(30, ChronoUnit.DAYS),
    )
}

