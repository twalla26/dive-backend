package com.twalla.divebackend.post

import com.twalla.divebackend.user.AuthorResponse
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.openapitools.jackson.nullable.JsonNullable
import java.time.Instant

data class CreatePostRequest(
    @field:NotBlank
    @field:Size(max = 5000, message = "본문은 5000자를 초과할 수 없습니다.")
    val content: String,
)

data class GetPostsResponse(
    val posts: List<GetPostSummaryResponse>
)

data class GetPostSummaryResponse(
    val id: Long,
    val content: String,
    val commentCount: Int,
    val likeCount: Int,
    val createdAt: Instant,
    val author: AuthorResponse,
)

data class PostDetailResponse(
    val id: Long,
    val content: String,
    val viewCount: Long,
    val commentCount: Int,
    val likeCount: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
    val author: AuthorResponse,
)

data class UpdatePostRequest(
    val content: JsonNullable<String> = JsonNullable.undefined(),
)

data class DeletePostResponse(
    val id: Long,
    val deletedAt: Instant,
    val restorableUntil: Instant,
)