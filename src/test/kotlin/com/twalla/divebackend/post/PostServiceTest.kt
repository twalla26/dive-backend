package com.twalla.divebackend.post

import com.twalla.divebackend.global.error.BusinessException
import com.twalla.divebackend.global.error.PostErrorCode
import com.twalla.divebackend.user.User
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.util.ReflectionTestUtils

class PostServiceTest {

    private val postRepository: PostRepository = mockk()
    private val postLikeRepository: PostLikeRepository = mockk()
    private val postService = PostService(
        postRepository = postRepository,
        postLikeRepository = postLikeRepository,
    )

    private val targetPostId = 100L
    private val authorUser = createUser(id = 1L)
    private val otherUser = createUser(id = 2L)

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("좋아요(likePost) 테스트")
    inner class LikePostTest {

        @Test
        @DisplayName("게시글이 존재하지 않거나 삭제되었으면 POST_NOT_FOUND 예외가 발생한다")
        fun likePost_postNotFound() {
            // given
            every { postRepository.findByIdAndDeletedAtIsNull(targetPostId) } returns null

            // when & then
            assertThatThrownBy { postService.likePost(otherUser, targetPostId) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", PostErrorCode.POST_NOT_FOUND)
        }

        @Test
        @DisplayName("본인이 작성한 게시글에 좋아요를 누르면 CANNOT_LIKE_OWN_POST 예외가 발생한다")
        fun likePost_cannotLikeOwnPost() {
            // given
            val post = createPost(id = targetPostId, user = authorUser)
            every { postRepository.findByIdAndDeletedAtIsNull(targetPostId) } returns post

            // when & then
            assertThatThrownBy { postService.likePost(authorUser, targetPostId) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", PostErrorCode.CANNOT_LIKE_OWN_POST)
        }

        @Test
        @DisplayName("이미 좋아요를 누른 상태라면 추가 저장 없이 기존 좋아요 수를 반환한다")
        fun likePost_alreadyLiked() {
            // given
            val post = createPost(id = targetPostId, user = authorUser, likeCount = 5)
            every { postRepository.findByIdAndDeletedAtIsNull(targetPostId) } returns post
            every { postLikeRepository.existsByPostIdAndUserId(targetPostId, otherUser.id) } returns true

            // when
            val response = postService.likePost(otherUser, targetPostId)

            // then
            assertThat(response.likeCount).isEqualTo(5)
            verify(exactly = 0) { postLikeRepository.save(any()) }
            verify(exactly = 0) { postRepository.increaseLikeCountById(any()) }
        }

        @Test
        @DisplayName("동시 요청 등으로 save 중 DB 중복 예외가 터져도 예외를 삼키고 기존 좋아요 수를 반환한다")
        fun likePost_duplicateKeyExceptionHandled() {
            // given
            val post = createPost(id = targetPostId, user = authorUser, likeCount = 5)
            every { postRepository.findByIdAndDeletedAtIsNull(targetPostId) } returns post
            every { postLikeRepository.existsByPostIdAndUserId(targetPostId, otherUser.id) } returns false
            every { postLikeRepository.save(any()) } throws DataIntegrityViolationException("Unique constraint")

            // when
            val response = postService.likePost(otherUser, targetPostId)

            // then
            assertThat(response.likeCount).isEqualTo(5)
            verify(exactly = 0) { postRepository.increaseLikeCountById(any()) }
        }

        @Test
        @DisplayName("정상적으로 좋아요가 등록되면 저장 및 카운트 증가 후 +1 보정된 좋아요 수를 반환한다")
        fun likePost_success() {
            // given
            val post = createPost(id = targetPostId, user = authorUser, likeCount = 5)
            every { postRepository.findByIdAndDeletedAtIsNull(targetPostId) } returns post
            every { postLikeRepository.existsByPostIdAndUserId(targetPostId, otherUser.id) } returns false
            every { postLikeRepository.save(any()) } returns mockk()
            every { postRepository.increaseLikeCountById(targetPostId) } returns 1

            // when
            val response = postService.likePost(otherUser, targetPostId)

            // then
            assertThat(response.likeCount).isEqualTo(6)
            verify(exactly = 1) {
                postLikeRepository.save(
                    match { it.post.id == targetPostId && it.user.id == otherUser.id }
                )
            }
            verify(exactly = 1) { postRepository.increaseLikeCountById(targetPostId) }
        }
    }

    @Nested
    @DisplayName("좋아요 취소(unlikePost) 테스트")
    inner class UnlikePostTest {

        @Test
        @DisplayName("게시글이 존재하지 않거나 삭제되었으면 POST_NOT_FOUND 예외가 발생한다")
        fun unlikePost_postNotFound() {
            // given
            every { postRepository.findByIdAndDeletedAtIsNull(targetPostId) } returns null

            // when & then
            assertThatThrownBy { postService.unlikePost(otherUser, targetPostId) }
                .isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", PostErrorCode.POST_NOT_FOUND)
        }

        @Test
        @DisplayName("좋아요를 누른 적이 없으면 삭제를 시도하지 않고 기존 좋아요 수를 반환한다")
        fun unlikePost_notLiked() {
            // given
            val post = createPost(id = targetPostId, user = authorUser, likeCount = 5)
            every { postRepository.findByIdAndDeletedAtIsNull(targetPostId) } returns post
            every { postLikeRepository.existsByPostIdAndUserId(targetPostId, otherUser.id) } returns false

            // when
            val response = postService.unlikePost(otherUser, targetPostId)

            // then
            assertThat(response.likeCount).isEqualTo(5)
            verify(exactly = 0) { postLikeRepository.deleteByPostIdAndUserId(any(), any()) }
            verify(exactly = 0) { postRepository.decreaseLikeCountById(any()) }
        }

        @Test
        @DisplayName("동시 요청 등으로 삭제된 row 수가 0이면 카운트를 감소시키지 않고 기존 좋아요 수를 반환한다")
        fun unlikePost_deleteCountZero() {
            // given
            val post = createPost(id = targetPostId, user = authorUser, likeCount = 5)
            every { postRepository.findByIdAndDeletedAtIsNull(targetPostId) } returns post
            every { postLikeRepository.existsByPostIdAndUserId(targetPostId, otherUser.id) } returns true
            every { postLikeRepository.deleteByPostIdAndUserId(targetPostId, otherUser.id) } returns 0

            // when
            val response = postService.unlikePost(otherUser, targetPostId)

            // then
            assertThat(response.likeCount).isEqualTo(5)
            verify(exactly = 0) { postRepository.decreaseLikeCountById(any()) }
        }

        @Test
        @DisplayName("정상적으로 좋아요가 취소되면 row 삭제 및 카운트 감소 후 -1 보정된 좋아요 수를 반환한다")
        fun unlikePost_success() {
            // given
            val post = createPost(id = targetPostId, user = authorUser, likeCount = 5)
            every { postRepository.findByIdAndDeletedAtIsNull(targetPostId) } returns post
            every { postLikeRepository.existsByPostIdAndUserId(targetPostId, otherUser.id) } returns true
            every { postLikeRepository.deleteByPostIdAndUserId(targetPostId, otherUser.id) } returns 1
            every { postRepository.decreaseLikeCountById(targetPostId) } returns 1

            // when
            val response = postService.unlikePost(otherUser, targetPostId)

            // then
            assertThat(response.likeCount).isEqualTo(4)
            verify(exactly = 1) { postLikeRepository.deleteByPostIdAndUserId(targetPostId, otherUser.id) }
            verify(exactly = 1) { postRepository.decreaseLikeCountById(targetPostId) }
        }
    }

    private fun createUser(
        id: Long,
    ): User {
        val user = User(
            email = "user$id@nyummy.com",
            password = "encodedPassword",
            nickname = "테스트유저",
        )
        ReflectionTestUtils.setField(user, "id", id)
        return user
    }

    private fun createPost(
        id: Long,
        user: User,
        likeCount: Int? = 0,
    ): Post {
        val post = Post(
            content = "테스트 내용",
            user = user,
        )
        ReflectionTestUtils.setField(post, "id", id)
        ReflectionTestUtils.setField(post, "likeCount", likeCount)
        return post
    }

}