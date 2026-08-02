package com.twalla.divebackend.auth

import com.twalla.divebackend.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: Argon2PasswordEncoder,
    private val jwtProvider: JwtProvider,
    private val jwtProperties: JwtProperties,
) {

    @Transactional
    fun signUp(request: SignUpRequest): SignUpResponse {

        if (userRepository.existsByEmail(request.email)) {
            throw RuntimeException("이미 가입된 이메일입니다: ${request.email}")
        }

        val hashedPassword = passwordEncoder.encode(request.password)

        val user = request.toUser(hashedPassword)

        val savedUser = userRepository.save(user)

        return savedUser.toSignUpResponse()
    }

    @Transactional
    fun login(request: LoginRequest): LoginResponse {

        val user = userRepository.findByEmail(request.email)
            ?: throw RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다.")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다.")
        }

        val userId = requireNotNull(user.id)

        val newAccessToken = jwtProvider.generateAccessToken(userId)
        val newRefreshToken = jwtProvider.generateRefreshToken(userId)
        val newExpiresAt = Instant.now().plusMillis(jwtProperties.refreshTokenExpirationMs)

        val existing = refreshTokenRepository.findByUserId(userId)
        if (existing != null) {
            existing.rotate(newRefreshToken, newExpiresAt)
        } else {
            refreshTokenRepository.save(
                RefreshToken(
                    user = user,
                    refreshToken = newRefreshToken,
                    expiresAt = Instant.now().plusMillis(jwtProperties.refreshTokenExpirationMs),
                )
            )
        }

        return LoginResponse(accessToken = newAccessToken, refreshToken = newRefreshToken)
    }

    @Transactional
    fun refresh(request: RefreshRequest): RefreshResponse {

        if (!jwtProvider.validateToken(request.refreshToken)) {
            throw RuntimeException("유효하지 않은 refresh token입니다.")
        }

        val savedToken = refreshTokenRepository.findByRefreshToken(request.refreshToken)
            ?: throw RuntimeException("유효하지 않은 refresh token입니다.")


        if (savedToken.isExpired()) {
            throw RuntimeException("만료된 refresh token입니다. 다시 로그인해주세요.")
        }

        val userId = requireNotNull(savedToken.user.id)

        val newAccessToken = jwtProvider.generateAccessToken(userId)
        val newRefreshToken = jwtProvider.generateRefreshToken(userId)
        val newExpiresAt = Instant.now().plusMillis(jwtProperties.refreshTokenExpirationMs)

        val existing = refreshTokenRepository.findByUserId(userId)
        if (existing != null) {
            existing.rotate(newRefreshToken, newExpiresAt)
        } else {
            refreshTokenRepository.save(
                RefreshToken(
                    user = savedToken.user,
                    refreshToken = newRefreshToken,
                    expiresAt = Instant.now().plusMillis(jwtProperties.refreshTokenExpirationMs),
                )
            )
        }

        return RefreshResponse(accessToken = newAccessToken, refreshToken = newRefreshToken)
    }
}