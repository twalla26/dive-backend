package com.twalla.divebackend.auth

import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByRefreshToken(token: String): RefreshToken?
    fun findByUserId(userId: Long): RefreshToken?
}