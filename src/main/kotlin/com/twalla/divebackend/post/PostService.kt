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

    fun getPosts(): GetPostsResponse {
        val posts = postRepository.findAllByDeletedAtIsNull(Sort.by(Sort.Direction.DESC, "createdAt"))

        val response = posts.map {
            it.toGetPostSummaryResponse()
        }

        return GetPostsResponse(response)
    }

    fun createPost(userId: Long, request: CreatePostRequest) {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "존재하지 않는 유저입니다: $userId"
            )

        val post = request.toPost(user)

        postRepository.save(post)
    }

    @Transactional
    fun getPost(postId: Long): GetPostDetailResponse {
        val post = postRepository.findByIdAndDeletedAtIsNull(postId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "존재하지 않는 게시글입니다: $postId"
            )

        post.increaseViewCount()

        return post.toGetPostDetailResponse()
    }
}