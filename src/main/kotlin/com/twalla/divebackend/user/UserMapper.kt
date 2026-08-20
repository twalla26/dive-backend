package com.twalla.divebackend.user

fun User.toMeResponse(): MeResponse {
    return MeResponse(
        userId = this.id,
        email = this.email,
    )
}