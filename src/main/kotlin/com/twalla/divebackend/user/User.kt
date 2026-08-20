package com.twalla.divebackend.user

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "users", schema = "dive")
@EntityListeners(AuditingEntityListener::class)
class User(
    @Column(name = "email", nullable = false, length = 100, updatable = false)
    val email: String,

    @Column(name = "password", nullable = false)
    var password: String,

    @Column(name = "nickname", nullable = false, length = 50)
    var nickname: String,
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

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null
        protected set

    fun delete() {
        if (deletedAt == null)
            return
        deletedAt = Instant.now()
    }
}