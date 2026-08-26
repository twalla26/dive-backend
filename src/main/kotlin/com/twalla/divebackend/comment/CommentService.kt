package com.twalla.divebackend.comment

import com.twalla.divebackend.post.PostRepository
import com.twalla.divebackend.user.User
import com.twalla.divebackend.user.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) {

    @Transactional(readOnly = true)
    fun getComments(postId: Long): CommentsResponse {

        postRepository.findByIdAndDeletedAtIsNull(postId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "존재하지 않는 게시글입니다: $postId",
            )

        val comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId)

        val response = comments.map {
            it.toCommentResponse()
        }

        return CommentsResponse(response)
    }

    @Transactional
    fun createComment(user: User, postId: Long, request: CreateCommentRequest): CommentResponse {

        val post = postRepository.findByIdAndDeletedAtIsNull(postId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "존재하지 않는 게시글입니다: $postId",
            )

        val comment = request.toComment(post, user)
        val savedComment = commentRepository.save(comment)
        postRepository.increaseCommentCountById(postId)

        return savedComment.toCommentResponse()
    }

    @Transactional
    fun deleteComment(user: User, commentId: Long) {

        val comment = commentRepository.findByIdOrNull(commentId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "존재하지 않는 댓글입니다: $commentId"
            )

        if (comment.user.id != user.id) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "해당 댓글에 대한 권한이 없습니다.",
            )
        }

        commentRepository.delete(comment)
        postRepository.decreaseCommentCountById(comment.post.id)
    }
}