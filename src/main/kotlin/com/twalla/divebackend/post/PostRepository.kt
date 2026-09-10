package com.twalla.divebackend.post

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface PostRepository : JpaRepository<Post, Long> {

    fun findAllByDeletedAtIsNull(pageable: Pageable): Page<Post>

    fun findAllByUserIdAndDeletedAtIsNull(userId: Long, pageable: Pageable): Page<Post>

    fun findByIdAndDeletedAtIsNull(id: Long): Post?

    @Modifying
    @Query("update Post p set p.viewCount = p.viewCount + 1 where p.id = :id and p.deletedAt is null")
    fun increaseViewCountById(id: Long): Int

    @Modifying
    @Query("update Post p set p.commentCount = p.commentCount + 1 where p.id = :id and p.deletedAt is null")
    fun increaseCommentCountById(id: Long): Int

    @Modifying
    @Query("update Post p set p.commentCount = p.commentCount - 1 where p.id = :id and p.commentCount > 0 and p.deletedAt is null")
    fun decreaseCommentCountById(id: Long): Int

    @Modifying
    @Query("update Post p set p.likeCount = p.likeCount + 1 where p.id = :id and p.deletedAt is null")
    fun increaseLikeCountById(id: Long): Int

    @Modifying
    @Query("update Post p set p.likeCount = p.likeCount - 1 where p.id = :id and p.likeCount > 0 and p.deletedAt is null")
    fun decreaseLikeCountById(id: Long): Int
}