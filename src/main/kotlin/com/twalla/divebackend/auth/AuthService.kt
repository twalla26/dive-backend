package com.twalla.divebackend.auth

import com.twalla.divebackend.global.error.AuthErrorCode
import com.twalla.divebackend.global.error.BusinessException
import com.twalla.divebackend.security.Argon2PasswordEncoder
import com.twalla.divebackend.security.JwtProperties
import com.twalla.divebackend.security.JwtProvider
import com.twalla.divebackend.user.UserRepository
import org.slf4j.LoggerFactory
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

    private val log = LoggerFactory.getLogger(AuthService::class.java)

    @Transactional
    fun signUp(request: SignUpRequest): SignUpResponse {

        if (userRepository.existsByEmail(request.email)) {
            throw BusinessException(AuthErrorCode.DUPLICATE_EMAIL)
        }

        val hashedPassword = passwordEncoder.encode(request.password)

        val user = request.toUser(hashedPassword)

        val savedUser = userRepository.save(user)

        log.info("회원가입: userId={}", savedUser.id)
        return savedUser.toSignUpResponse()
    }

    @Transactional
    fun login(request: LoginRequest): LoginResponse {

        val user = userRepository.findByEmail(request.email)
            ?: run {
                log.warn("로그인 실패: 존재하지 않는 이메일")
                throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
            }

        if (!passwordEncoder.matches(request.password, user.password)) {
            log.warn("로그인 실패: userId={}", user.id)
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        val userId = user.id

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

        log.info("로그인 성공: userId={}", userId)
        return LoginResponse(accessToken = newAccessToken, refreshToken = newRefreshToken)
    }

    @Transactional
    fun refresh(request: RefreshRequest): RefreshResponse {

        if (!jwtProvider.validateToken(request.refreshToken)) {
            throw BusinessException(AuthErrorCode.INVALID_TOKEN)
        }

        val savedToken = refreshTokenRepository.findByRefreshToken(request.refreshToken)
            ?: throw BusinessException(AuthErrorCode.INVALID_TOKEN)

        if (savedToken.isExpired()) {
            throw BusinessException(AuthErrorCode.INVALID_TOKEN)
        }

        val userId = savedToken.user.id

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

        log.info("토큰 재발급: userId={}", userId)
        return RefreshResponse(accessToken = newAccessToken, refreshToken = newRefreshToken)
    }
}