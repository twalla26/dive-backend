package com.twalla.divebackend.auth

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "jwt")
class JwtProperties {
    lateinit var secret: String
    var accessTokenExpirationMs: Long = 3600000L // 1시간
    var refreshTokenExpirationMs: Long = 1209600000L // 14일
}