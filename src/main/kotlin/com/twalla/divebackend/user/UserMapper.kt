package com.twalla.divebackend.user

fun User.toMeResponse(): MeResponse {
    return MeResponse(
        userId = this.id,
        email = this.email,
    )
}

fun User.toAuthorResponse(): AuthorResponse {
    return AuthorResponse(
        userId = this.id,
        nickname = this.nickname,
    )
}