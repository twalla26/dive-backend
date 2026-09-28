package com.twalla.divebackend.global.logging

import com.twalla.divebackend.security.JwtAuthInterceptor
import com.twalla.divebackend.user.User
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RequestLoggingFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val start = System.nanoTime()
        MDC.put(REQUEST_ID, UUID.randomUUID().toString().take(8))

        try {
            filterChain.doFilter(request, response)
        } finally {
            val took = (System.nanoTime() - start) / 1_000_000
            val query = request.queryString?.let { "?$it" } ?: ""
            val userId = (request.getAttribute(JwtAuthInterceptor.USER_ATTRIBUTE) as? User)?.id

            log.info(
                "{} {}{} {} {}ms userId={}",
                request.method,
                request.requestURI,
                query,
                response.status,
                took,
                userId ?: "-",
            )

            MDC.clear()
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        request.requestURI.startsWith("/actuator")

    companion object {
        const val REQUEST_ID = "requestId"
    }

}