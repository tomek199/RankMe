package com.tm.rankme.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan


@SpringBootApplication
@ComponentScan(value = ["com.tm.rankme.application", "com.tm.rankme.infrastructure"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
