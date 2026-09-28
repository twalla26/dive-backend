package com.twalla.divebackend.comment

import com.twalla.divebackend.global.error.BusinessException
import com.twalla.divebackend.global.error.CommentErrorCode
import com.twalla.divebackend.global.error.PostErrorCode
import com.twalla.divebackend.post.PostRepository
import com.twalla.divebackend.user.User
import com.twalla.divebackend.user.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) {

    private val log = LoggerFactory.getLogger(CommentService::class.java)

    @Transactional(readOnly = true)
    fun getComments(postId: Long): CommentsResponse {

        postRepository.findByIdAndDeletedAtIsNull(postId)
            ?: throw BusinessException(PostErrorCode.POST_NOT_FOUND)

        val comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId)

        val response = comments.map {
            it.toCommentResponse()
        }

        return CommentsResponse(response)
    }

    @Transactional
    fun createComment(user: User, postId: Long, request: CreateCommentRequest): CommentResponse {

        val post = postRepository.findByIdAndDeletedAtIsNull(postId)
            ?: throw BusinessException(PostErrorCode.POST_NOT_FOUND)

        val comment = request.toComment(post, user)
        val savedComment = commentRepository.save(comment)
        postRepository.increaseCommentCountById(postId)

        log.info("댓글 생성: commentId={}, postId={}, userId={}", savedComment.id, postId, user.id)
        return savedComment.toCommentResponse()
    }

    @Transactional
    fun deleteComment(user: User, commentId: Long) {

        val comment = commentRepository.findByIdOrNull(commentId)
            ?: throw BusinessException(CommentErrorCode.COMMENT_NOT_FOUND)

        if (comment.user.id != user.id) {
            throw BusinessException(CommentErrorCode.COMMENT_ACCESS_DENIED)
        }

        commentRepository.delete(comment)
        postRepository.decreaseCommentCountById(comment.post.id)
        log.info("댓글 삭제: commentId={}, userId={}", commentId, user.id)
    }
}