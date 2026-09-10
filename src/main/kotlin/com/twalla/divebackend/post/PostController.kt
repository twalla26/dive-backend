package com.twalla.divebackend.post

import com.twalla.divebackend.auth.AuthUser
import com.twalla.divebackend.user.User
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/posts")
class PostController(
    private val postService: PostService,
) {

    @GetMapping()
    fun getPosts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<PostListResponse> {
        val response = postService.getPosts(page, size)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping()
    fun createPost(
        @AuthUser user: User,
        @Valid @RequestBody request: CreatePostRequest,
    ): ResponseEntity<PostDetailResponse> {
        val response = postService.createPost(user, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{postId}")
    fun getPost(@PathVariable postId: Long): ResponseEntity<PostDetailResponse> {
        val response = postService.getPost(postId)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PatchMapping("/{postId}")
    fun updatePost(
        @AuthUser user: User,
        @PathVariable postId: Long,
        @Valid @RequestBody request: UpdatePostRequest,
    ): ResponseEntity<PostDetailResponse> {
        val response = postService.updatePost(user, postId, request)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @DeleteMapping("/{postId}")
    fun deletePost(
        @AuthUser user: User,
        @PathVariable postId: Long,
    ): ResponseEntity<DeletePostResponse> {
        val response = postService.deletePost(user, postId)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping("/{postId}/likes")
    fun likePost(
        @AuthUser user: User,
        @PathVariable postId: Long,
    ): ResponseEntity<PostLikeResponse> {
        val response = postService.likePost(user, postId)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @DeleteMapping("/{postId}/likes")
    fun unlikePost(
        @AuthUser user: User,
        @PathVariable postId: Long,
    ): ResponseEntity<PostLikeResponse> {
        val response = postService.unlikePost(user, postId)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

}