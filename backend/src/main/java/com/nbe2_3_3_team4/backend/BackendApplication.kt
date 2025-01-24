package com.nbe2_3_3_team4.backend

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class BackendApplication
    fun main(args: Array<String>) {
        runApplication<BackendApplication>(*args)
    }
