package com.twalla.divebackend.post

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
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
    val userId: Long,
    val nickname: String,
)

data class GetPostDetailResponse(
    val id: Long,
    val content: String,
    val viewCount: Long,
    val commentCount: Int,
    val likeCount: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
    val userId: Long,
    val nickname: String,
)