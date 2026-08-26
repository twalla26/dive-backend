package com.twalla.divebackend.user

import com.twalla.divebackend.auth.AuthUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("me")
    fun me(@AuthUser user: User): MeResponse {
        return userService.me(user)
    }
}