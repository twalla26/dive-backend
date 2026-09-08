package com.twalla.divebackend.post

import org.springframework.data.jpa.repository.JpaRepository

interface PostLikeRepository : JpaRepository<PostLike, Long> {
    fun existsByPostIdAndUserId(postId: Long, userId: Long): Boolean

    fun findByPostIdAndUserId(postId: Long, userId: Long): PostLike?

    fun deleteByPostIdAndUserId(postId: Long, userId: Long): Int
}