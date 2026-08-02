package com.twalla.divebackend.auth

import com.twalla.divebackend.user.User
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "refresh_token", schema = "dive")
@EntityListeners(AuditingEntityListener::class)
class RefreshToken(
    @Column(name = "refresh_token", nullable = false, length = 512)
    var refreshToken: String,

    @Column(name = "expires_at", nullable = false)
    var expiresAt: Instant,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null
        protected set

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant? = null
        protected set

    fun isExpired(at: Instant = Instant.now()): Boolean {
        return expiresAt.isBefore(at)
    }

    fun rotate(newToken: String, newExpiresAt: Instant) {
        refreshToken = newToken
        expiresAt = newExpiresAt
    }
}