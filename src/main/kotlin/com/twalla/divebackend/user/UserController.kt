package com.twalla.divebackend.user

import com.twalla.divebackend.auth.JwtAuthInterceptor
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("me")
    fun me(request: HttpServletRequest): MeResponse {
        val userId = request.getAttribute(JwtAuthInterceptor.USER_ID_ATTRIBUTE) as Long
        return userService.me(userId)
    }
}