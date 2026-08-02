package com.twalla.divebackend.auth

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import org.springframework.stereotype.Component

@Component
class Argon2PasswordEncoder {

    private val argon2: Argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)

    companion object {
        private const val ITERATIONS = 3
        private const val MEMORY_KB = 65536
        private const val PARALLELISM = 1
    }

    fun encode(rawPassword: String): String {
        val chars = rawPassword.toCharArray()
        return try {
            argon2.hash(ITERATIONS, MEMORY_KB, PARALLELISM, chars)
        } finally {
            argon2.wipeArray(chars)
        }
    }

    fun matches(rawPassword: String, encodedPassword: String): Boolean {
        val chars = rawPassword.toCharArray()
        return try {
            argon2.verify(encodedPassword, chars)
        } finally {
            argon2.wipeArray(chars)
        }
    }
}