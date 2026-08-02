package com.twalla.divebackend.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SignUpRequest(
    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    @field:Size(max = 100)
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    @field:Size(min = 8, max = 64, message = "비밀번호는 8~64자여야 합니다.")
    @field:Pattern(
        regexp = """^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$""",
        message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    val password: String,

    @field:NotBlank(message = "닉네임은 필수입니다.")
    @field:Size(max = 50)
    val nickname: String,
)

data class SignUpResponse(
    val email: String,
    val nickname: String,
)

data class LoginRequest(
    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Size(max = 100)
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    val password: String,
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
)

data class RefreshRequest(
    @field:NotBlank()
    val refreshToken: String,
)

data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String,
)