package com.twalla.divebackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class DiveBackendApplication

fun main(args: Array<String>) {
    runApplication<DiveBackendApplication>(*args)
}
