package com.twalla.divebackend.post

import com.twalla.divebackend.user.User
import java.time.temporal.ChronoUnit

fun CreatePostRequest.toPost(user: User): Post {
    return Post(
        content = this.content,
        user = user,
    )
}

fun Post.toGetPostSummaryResponse(): GetPostSummaryResponse {
    return GetPostSummaryResponse(
        id = requireNotNull(this.id),
        content = this.content.take(100),
        commentCount = this.commentCount,
        likeCount = this.likeCount,
        createdAt = requireNotNull(this.createdAt),
        userId = requireNotNull(this.user.id),
        nickname = this.user.nickname,
    )
}

fun Post.toPostDetailResponse(): PostDetailResponse {
    return PostDetailResponse(
        id = requireNotNull(this.id),
        content = this.content,
        viewCount = this.viewCount,
        commentCount = this.commentCount,
        likeCount = this.likeCount,
        createdAt = requireNotNull(this.createdAt),
        updatedAt = requireNotNull(this.updatedAt),
        userId = requireNotNull(this.user.id),
        nickname = this.user.nickname,
    )
}

fun Post.toDeletePostResponse(): DeletePostResponse {
    return DeletePostResponse(
        id = requireNotNull(this.id),
        deletedAt = requireNotNull(this.deletedAt),
        restorableUntil = requireNotNull(this.deletedAt).plus(30, ChronoUnit.DAYS),
    )
}

