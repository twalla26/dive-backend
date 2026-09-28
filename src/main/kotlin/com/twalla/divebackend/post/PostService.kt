package com.twalla.divebackend.post

import com.twalla.divebackend.global.error.BusinessException
import com.twalla.divebackend.global.error.PostErrorCode
import com.twalla.divebackend.user.User
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostService(
    private val postRepository: PostRepository,
    private val postLikeRepository: PostLikeRepository,
) {

    private val log = LoggerFactory.getLogger(PostService::class.java)

    @Transactional(readOnly = true)
    fun getPosts(page: Int, size: Int): PostListResponse {

        val pageable = PageRequest.of(
            page,
            size,
            Sort.by(Sort.Direction.DESC, "createdAt")
        )

        val posts = postRepository.findAllByDeletedAtIsNull(pageable)

        val postSummaries = posts.content.map {
            it.toPostSummaryResponse()
        }

        return posts.toPostListResponse(postSummaries)
    }

    @Transactional(readOnly = true)
    fun getPostsByAuthor(user: User, page: Int, size: Int): PostListResponse {

        val pageable = PageRequest.of(
            page,
            size,
            Sort.by(Sort.Direction.DESC, "createdAt")
        )

        val posts = postRepository.findAllByUserIdAndDeletedAtIsNull(userId = user.id, pageable)

        val postSummaries = posts.content.map {
            it.toPostSummaryResponse()
        }

        return posts.toPostListResponse(postSummaries)
    }

    @Transactional
    fun createPost(user: User, request: CreatePostRequest): PostDetailResponse {
        val post = request.toPost(user)
        val savedPost = postRepository.save(post)

        log.info("게시글 생성: postId={}, userId={}", savedPost.id, user.id)
        return savedPost.toPostDetailResponse()
    }

    @Transactional
    fun getPost(postId: Long): PostDetailResponse { // ~AndIncreaseViewCount 함수 두개를 목적에 맞게 구현!

        postRepository.increaseViewCountById(postId)

        val post = postRepository.findByIdAndDeletedAtIsNull(postId)
            ?: throw BusinessException(PostErrorCode.POST_NOT_FOUND)

        return post.toPostDetailResponse()
    }

    @Transactional
    fun updatePost(user: User, postId: Long, request: UpdatePostRequest): PostDetailResponse {

        val post = postRepository.findByIdAndDeletedAtIsNull(postId)
            ?: throw BusinessException(PostErrorCode.POST_NOT_FOUND)

        if (post.user.id != user.id) {
            throw BusinessException(PostErrorCode.POST_ACCESS_DENIED)
        }

        if (request.content.isPresent) {
            post.updateContent(request.content.get())
        }

        log.info("게시글 수정: postId={}, userId={}", postId, user.id)
        return post.toPostDetailResponse()
    }

    @Transactional
    fun deletePost(user: User, postId: Long): DeletePostResponse {

        val post = postRepository.findByIdAndDeletedAtIsNull(postId)
            ?: throw BusinessException(PostErrorCode.POST_NOT_FOUND)

        if (post.user.id != user.id) {
            throw BusinessException(PostErrorCode.POST_ACCESS_DENIED)
        }

        post.delete()

        log.info("게시글 삭제: postId={}, userId={}", postId, user.id)
        return post.toDeletePostResponse()
    }

    @Transactional
    fun likePost(user: User, postId: Long): PostLikeResponse {

        val post = postRepository.findByIdAndDeletedAtIsNull(postId)
            ?: throw BusinessException(PostErrorCode.POST_NOT_FOUND)

        if (post.user.id == user.id) {
            throw BusinessException(PostErrorCode.CANNOT_LIKE_OWN_POST)
        }

        if (postLikeRepository.existsByPostIdAndUserId(postId, user.id)) {
            return PostLikeResponse(likeCount = post.likeCount)
        }

        try {
            postLikeRepository.save(PostLike(post, user))
        } catch (e: Exception) {
            log.warn("좋아요 중복 저장 시도: postId={}, userId={}", postId, user.id)
            return PostLikeResponse(likeCount = post.likeCount)
        }

        postRepository.increaseLikeCountById(postId) // 벌크 UPDATE

        log.info("좋아요: postId={}, userId={}", postId, user.id)
        // 벌크 UPDATE는 영속성 컨텍스트에 반영되지 않으므로 직접 보정
        return PostLikeResponse(likeCount = post.likeCount + 1)
    }

    @Transactional
    fun unlikePost(user: User, postId: Long): PostLikeResponse {

        val post = postRepository.findByIdAndDeletedAtIsNull(postId)
            ?: throw BusinessException(PostErrorCode.POST_NOT_FOUND)

        if (!postLikeRepository.existsByPostIdAndUserId(postId, user.id)) {
            return PostLikeResponse(likeCount = post.likeCount)
        }

        val deleted = postLikeRepository.deleteByPostIdAndUserId(post.id, user.id)

        if (deleted == 0) {
            return PostLikeResponse(likeCount = post.likeCount)
        }

        postRepository.decreaseLikeCountById(postId) // 벌크 UPDATE

        log.info("좋아요 취소: postId={}, userId={}", postId, user.id)
        // 벌크 UPDATE는 영속성 컨텍스트에 반영되지 않으므로 직접 보정
        return PostLikeResponse(likeCount = post.likeCount - 1)
    }

}