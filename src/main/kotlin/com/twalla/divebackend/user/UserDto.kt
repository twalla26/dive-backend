package com.twalla.divebackend.user

data class MeResponse(
    val userId: Long,
    val email: String,
)

data class AuthorResponse(
    val userId: Long,
    val nickname: String,
)