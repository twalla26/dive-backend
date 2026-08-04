package com.twalla.divebackend.post

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long> {
    
    @EntityGraph(attributePaths = ["user"])
    fun findAllByDeletedAtIsNull(sort: Sort): List<Post>

    @EntityGraph(attributePaths = ["user"])
    fun findByIdAndDeletedAtIsNull(id: Long): Post?
}