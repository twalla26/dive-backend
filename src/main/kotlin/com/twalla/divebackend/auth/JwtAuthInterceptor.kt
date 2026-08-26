package com.twalla.divebackend.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class JwtAuthInterceptor(
    private val chain: List<AuthValidator>,
) : HandlerInterceptor {
    companion object {
        const val USER_ATTRIBUTE = "user"
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {

        val context = AuthContext(request)
        chain.forEach { it.validate(context) }

        request.setAttribute(USER_ATTRIBUTE, context.user)
        return true
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val header = request.getHeader("Authorization") ?: return null
        return if (header.startsWith("Bearer ")) header.substring(7) else null
    }
}