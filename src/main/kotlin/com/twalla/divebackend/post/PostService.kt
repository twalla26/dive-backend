package com.twalla.divebackend.post

import com.twalla.divebackend.user.UserRepository
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
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
        val posts = postRepository.findAllByDeletedAtIsNull(Sort.by(Sort.Direction.DESC, "createdAt"))

        val response = posts.map {
            it.toGetPostSummaryResponse()
        }

        return GetPostsResponse(response)
    }

    @Transactional
    fun createPost(userId: Long, request: CreatePostRequest) {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "존재하지 않는 유저입니다: $userId"
            )

        val post = request.toPost(user)

        postRepository.save(post)
    }

    @Transactional(readOnly = true)
    fun getPost(postId: Long): PostDetailResponse {
        val post = postRepository.findByIdAndDeletedAtIsNull(postId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "존재하지 않는 게시글입니다: $postId"
            )

        post.increaseViewCount()

        return post.toPostDetailResponse()
    }

    @Transactional
    fun updatePost(userId: Long, postId: Long, request: UpdatePostRequest): PostDetailResponse {

        val post = postRepository.findByIdAndDeletedAtIsNull(postId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "존재하지 않는 게시글입니다: $postId",
            )

        if (post.user.id != userId) {
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

}