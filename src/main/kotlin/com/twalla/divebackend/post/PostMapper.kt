package com.twalla.divebackend.post

import com.twalla.divebackend.user.User
import com.twalla.divebackend.user.toAuthorResponse
import org.springframework.data.domain.Page
import java.time.temporal.ChronoUnit


fun Page<Post>.toPostListResponse(posts: List<PostSummaryResponse>): PostListResponse {
    return PostListResponse(
        posts = posts,
        page = this.number,
        size = this.size,
        totalCount = this.totalElements,
        totalPages = this.totalPages,
    )
}

fun CreatePostRequest.toPost(user: User): Post {
    return Post(
        content = this.content,
        user = user,
    )
}

fun Post.toPostSummaryResponse(): PostSummaryResponse {
    return PostSummaryResponse(
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

