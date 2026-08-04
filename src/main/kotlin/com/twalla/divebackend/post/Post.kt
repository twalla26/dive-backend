package com.twalla.divebackend.post

import com.twalla.divebackend.user.User
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "post", schema = "dive")
@EntityListeners(AuditingEntityListener::class)
class Post(
    @Lob
    @Column(name = "content", nullable = false)
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
) {
    companion object {
        fun new(content: String, user: User): Post {
            return Post(content, user)
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null
        protected set

    @ColumnDefault("0")
    @Column(name = "view_count", nullable = false)
    var viewCount: Long = 0L
        protected set

    @ColumnDefault("0")
    @Column(name = "comment_count", nullable = false)
    var commentCount: Int = 0
        protected set

    @ColumnDefault("0")
    @Column(name = "like_count", nullable = false)
    var likeCount: Int = 0
        protected set

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant? = null
        protected set

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant? = null
        protected set

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null
        protected set

    fun isDeleted(): Boolean = deletedAt != null

    fun delete() {
        if (deletedAt == null) {
            deletedAt = Instant.now()
        }
    }

    fun edit(newContent: String) {
        content = newContent
    }

    fun increaseViewCount() {
        viewCount += 1
    }

    fun increaseCommentCount() {
        commentCount += 1
    }

    fun decreaseCommentCount() {
        if (commentCount > 0) {
            commentCount -= 1
        }
    }

    fun increaseLikeCount() {
        likeCount += 1
    }

    fun decreaseLikeCount() {
        if (likeCount > 0) {
            likeCount -= 1
        }
    }

}