package com.twalla.divebackend.post

import com.twalla.divebackend.user.User
import com.twalla.divebackend.user.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class PostService(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) {

    @Transactional(readOnly = true)
    fun getPosts(): GetPostsResponse {
        val posts = postRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc()

        val response = posts.map {
            it.toPostSummaryResponse()
        }

        return GetPostsResponse(response)
    }

    @Transactional
    fun createPost(user: User, request: CreatePostRequest): PostDetailResponse {
        val post = request.toPost(user)

        val savedPost = postRepository.save(post)

        return savedPost.toPostDetailResponse()
    }

    @Transactional
    fun getPost(postId: Long): PostDetailResponse { // ~AndIncreaseViewCount 함수 두개를 목적에 맞게 구현!

        postRepository.increaseViewCountById(postId)

        val post = postRepository.findByIdAndDeletedAtIsNull(postId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "존재하지 않는 게시글입니다: $postId"
            )

        return post.toPostDetailResponse()
    }

    @Transactional
    fun updatePost(user: User, postId: Long, request: UpdatePostRequest): PostDetailResponse {

        val post = postRepository.findByIdAndDeletedAtIsNull(postId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "존재하지 않는 게시글입니다: $postId",
            )

        if (post.user.id != user.id) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "해당 게시글에 대한 권한이 없습니다.",
            )
        }

        if (request.content.isPresent) {
            post.updateContent(request.content.get())
            postRepository.flush()
        }

        return post.toPostDetailResponse()
    }

    @Transactional
    fun deletePost(user: User, postId: Long): DeletePostResponse {

        val post = postRepository.findByIdAndDeletedAtIsNull(postId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "존재하지 않는 게시글입니다: $postId",
            )

        println("${post.user} + user")

        if (post.user.id != user.id) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "해당 게시글에 대한 권한이 없습니다.",
            )
        }

        post.delete()
        postRepository.flush()

        return post.toDeletePostResponse()
    }

}