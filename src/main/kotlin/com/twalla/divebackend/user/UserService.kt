package com.twalla.divebackend.user

import org.springframework.stereotype.Service

@Service
class UserService() {
    fun me(user: User): MeResponse {

        return user.toMeResponse()
    }
}