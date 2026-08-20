package com.twalla.divebackend.auth

import com.twalla.divebackend.user.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

class AuthContext(val request: HttpServletRequest) {
    var token: String? = null
    var userId: Long? = null

    val requiredToken: String
        get() = token ?: error("토큰 추출 단계가 선행되어야 합니다.")

    val requiredUserId: Long
        get() = userId ?: error("토큰 파싱 단계가 선행되어야 합니다")
}

fun interface AuthValidator {
    fun validate(context: AuthContext)
}

@Component
@Order(1)
class TokenPresenceValidator : AuthValidator {
    override fun validate(context: AuthContext) {
        val header = context.request.getHeader("Authorization")
            ?: throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Authorization 헤더가 없습니다.",
            )

        if (!header.startsWith("Bearer ")) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Bearer 토큰 형식이 아닙니다.",
            )
        }

        context.token = header.substring(7)
    }
}

@Component
@Order(2)
class TokenSignatureValidator(
    private val jwtProvider: JwtProvider
) : AuthValidator {
    override fun validate(context: AuthContext) {
        if (!jwtProvider.validateToken(context.requiredToken)) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "유효하지 않거나 만료된 토큰입니다.",
            )
        }

        context.userId = jwtProvider.getUserId(context.requiredToken)
    }
}

@Component
@Order(3)
class UserExistenceValidator(
    private val userRepository: UserRepository,
) : AuthValidator {
    override fun validate(context: AuthContext) {
        if (!userRepository.existsById(context.requiredUserId)) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "존재하지 않는 유저입니다.",
            )
        }
    }
}