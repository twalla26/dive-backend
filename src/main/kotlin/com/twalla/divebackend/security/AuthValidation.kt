package com.twalla.divebackend.security

import com.twalla.divebackend.global.error.AuthErrorCode
import com.twalla.divebackend.global.error.BusinessException
import com.twalla.divebackend.user.User
import com.twalla.divebackend.user.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

class AuthContext(val request: HttpServletRequest) {
    var token: String? = null
    var user: User? = null

    val requiredToken: String
        get() = token ?: error("토큰 추출 단계가 선행되어야 합니다.")

    val requiredUser: User
        get() = user ?: error("토큰 파싱 단계가 선행되어야 합니다")
}

fun interface AuthValidator {
    fun validate(context: AuthContext)
}

@Component
@Order(1)
class TokenPresenceValidator : AuthValidator {
    override fun validate(context: AuthContext) {
        val header = context.request.getHeader("Authorization")
            ?: throw BusinessException(AuthErrorCode.INVALID_TOKEN)

        if (!header.startsWith("Bearer ")) {
            throw BusinessException(AuthErrorCode.INVALID_TOKEN)
        }

        context.token = header.substring(7)
    }
}

@Component
@Order(2)
class TokenSignatureValidator(
    private val jwtProvider: JwtProvider,
    private val userRepository: UserRepository
) : AuthValidator {
    override fun validate(context: AuthContext) {
        if (!jwtProvider.validateToken(context.requiredToken)) {
            throw BusinessException(AuthErrorCode.INVALID_TOKEN)
        }

        val userId = jwtProvider.getUserId(context.requiredToken)

        context.user = userRepository.findByIdAndDeletedAtIsNull(userId)
            ?: throw BusinessException(AuthErrorCode.UNAUTHENTICATED)
    }
}