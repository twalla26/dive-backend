package com.twalla.divebackend.user

fun User.toMeResponse(): MeResponse {
    return MeResponse(
        userId = requireNotNull(this.id),
        email = this.email,
    )
}