package com.twalla.divebackend.comment

import com.twalla.divebackend.auth.AuthUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class CommentController(
    private val commentService: CommentService,
) {

    @GetMapping("/posts/{postId}/comments")
    fun getComments(
        @PathVariable postId: Long,
    ): ResponseEntity<GetCommentsResponse> {
        val response = commentService.getComments(postId)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping("/posts/{postId}/comments")
    fun createComment(
        @AuthUser userId: Long,
        @PathVariable postId: Long,
        @Valid @RequestBody request: CreateCommentRequest,
    ): ResponseEntity<CommentResponse> {
        val response = commentService.createComment(userId, postId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @DeleteMapping("/comments/{commentId}")
    fun deleteComment(
        @AuthUser userId: Long,
        @PathVariable commentId: Long,
    ): ResponseEntity<Void> {
        commentService.deleteComment(userId, commentId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

}