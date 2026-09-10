package com.twalla.divebackend.post

import com.twalla.divebackend.user.User
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class PostService(
    private val postRepository: PostRepository,
    private val postLikeRepository: PostLikeRepository,
) {

    @Transactional(readOnly = true)
    fun getPosts(): GetPostsResponse {
        val posts = postRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc()

        val response = posts.map {
            it.toPostSummaryResponse()
        }

        return GetPostsResponse(response)
    }

    @Transactional(readOnly = true)
    fun getPostsByAuthor(user: User): GetPostsResponse {
        val posts = postRepository.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId = user.id)

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

        if (post.user.id != user.id) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "해당 게시글에 대한 권한이 없습니다.",
            )
        }

        post.delete()

        return post.toDeletePostResponse()
    }

    @Transactional
    fun likePost(user: User, postId: Long): PostLikeResponse {

        val post = postRepository.findByIdAndDeletedAtIsNull(postId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "존재하지 않는 게시글입니다: $postId",
            )

        if (post.user.id == user.id) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "자신의 글에는 좋아요를 누를 수 없습니다."
            )
        }

        if (postLikeRepository.existsByPostIdAndUserId(postId, user.id)) {
            return PostLikeResponse(likeCount = post.likeCount)
        }

        try {
            postLikeRepository.save(PostLike(post, user))
        } catch (e: Exception) {
            return PostLikeResponse(likeCount = post.likeCount)
        }

        postRepository.increaseLikeCountById(postId) // 벌크 UPDATE

        // 벌크 UPDATE는 영속성 컨텍스트에 반영되지 않으므로 직접 보정
        return PostLikeResponse(likeCount = post.likeCount + 1)
    }

    @Transactional
    fun unlikePost(user: User, postId: Long): PostLikeResponse {

        val post = postRepository.findByIdAndDeletedAtIsNull(postId)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "존재하지 않는 게시글입니다: $postId",
            )

        if (!postLikeRepository.existsByPostIdAndUserId(postId, user.id)) {
            return PostLikeResponse(likeCount = post.likeCount)
        }

        val deleted = postLikeRepository.deleteByPostIdAndUserId(post.id, user.id)

        if (deleted == 0) {
            return PostLikeResponse(likeCount = post.likeCount)
        }

        postRepository.decreaseLikeCountById(postId) // 벌크 UPDATE

        // 벌크 UPDATE는 영속성 컨텍스트에 반영되지 않으므로 직접 보정
        return PostLikeResponse(likeCount = post.likeCount - 1)
    }

}