package com.twalla.divebackend.comment

import com.twalla.divebackend.user.AuthorResponse
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant


data class GetCommentsResponse(
    val comments: List<CommentResponse>,
)

data class CreateCommentRequest(
    @field:NotBlank
    @field:Size(max = 2000, message = "댓글은 최대 2000자까지 작성 가능합니다.")
    val content: String,
)

data class CommentResponse(
    val id: Long,
    val content: String,
    val likeCount: Int,
    val postId: Long,
    val author: AuthorResponse,
    val createdAt: Instant,
)