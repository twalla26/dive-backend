package com.twalla.divebackend.auth

import com.twalla.divebackend.user.User

fun SignUpRequest.toUser(encodedPassword: String): User {
    return User(
        email = this.email,
        password = encodedPassword,
        nickname = this.nickname,
    )
}

fun User.toSignUpResponse(): SignUpResponse {
    return SignUpResponse(
        email = this.email,
        nickname = this.nickname,
    )
}