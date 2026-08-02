package com.twalla.divebackend.user

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun me(userId: Long): MeResponse {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw RuntimeException("존재하지 않는 유저입니다: $userId")
        
        return user.toMeResponse()
    }
}