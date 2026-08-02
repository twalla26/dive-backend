package com.twalla.divebackend.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtProvider(
    private val jwtProperties: JwtProperties
) {
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())
    }

    fun generateAccessToken(userId: Long): String {
        return buildToken(userId, jwtProperties.accessTokenExpirationMs)
    }

    fun generateRefreshToken(userId: Long): String {
        return buildToken(userId, jwtProperties.refreshTokenExpirationMs)
    }

    private fun buildToken(userId: Long, expirationMs: Long): String {
        val now = Date()
        val expiry = Date(now.time + expirationMs)
        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    fun parseClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    fun validateToken(token: String): Boolean {
        return try {
            parseClaims(token)
            true
        } catch (e: ExpiredJwtException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    fun getExpiration(token: String): Date {
        return parseClaims(token).expiration
    }

    fun getUserId(token: String): Long {
        return parseClaims(token).subject.toLong()
    }
}