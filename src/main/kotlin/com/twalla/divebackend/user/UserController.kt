package com.twalla.divebackend.user

import com.twalla.divebackend.auth.AuthUser
import com.twalla.divebackend.post.GetPostsResponse
import com.twalla.divebackend.post.PostService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
    private val postService: PostService,
) {
    @GetMapping("/me")
    fun me(@AuthUser user: User): MeResponse {
        return userService.me(user)
    }

    @GetMapping("/me/posts")
    fun getMyPosts(@AuthUser user: User): GetPostsResponse {
        return postService.getPostsByAuthor(user)
    }
}