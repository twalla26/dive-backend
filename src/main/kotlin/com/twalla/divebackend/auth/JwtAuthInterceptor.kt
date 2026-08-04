package com.twalla.divebackend.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.HandlerInterceptor

@Component
class JwtAuthInterceptor(
    private val jwtProvider: JwtProvider,
) : HandlerInterceptor {
    companion object {
        const val USER_ID_ATTRIBUTE = "userId"
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        val token = extractToken(request)
            ?: throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Authorization 헤더가 없습니다."
            )

        if (!jwtProvider.validateToken(token)) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "유효하지 않거나 만료된 토큰입니다."
            )
        }

        request.setAttribute(USER_ID_ATTRIBUTE, jwtProvider.getUserId(token))
        return true
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val header = request.getHeader("Authorization") ?: return null
        return if (header.startsWith("Bearer ")) header.substring(7) else null
    }
}