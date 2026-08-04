package com.twalla.divebackend.post

import com.twalla.divebackend.auth.AuthUser
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
    fun getPosts(): ResponseEntity<GetPostsResponse> {
        val response = postService.getPosts()
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping()
    fun createPost(
        @AuthUser userId: Long,
        @Valid @RequestBody request: CreatePostRequest,
    ): ResponseEntity<Void> {
        postService.createPost(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping("/{postId}")
    fun getPost(@PathVariable postId: Long): ResponseEntity<PostDetailResponse> {
        val response = postService.getPost(postId)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PatchMapping("/{postId}")
    fun updatePost(
        @AuthUser userId: Long,
        @PathVariable postId: Long,
        @Valid @RequestBody request: UpdatePostRequest,
    ): ResponseEntity<PostDetailResponse> {
        val response = postService.updatePost(userId, postId, request)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

}