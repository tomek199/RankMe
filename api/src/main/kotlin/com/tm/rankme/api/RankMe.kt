package com.tm.rankme.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(value = [
    "com.tm.rankme.api",
    "com.tm.rankme.application",
    "com.tm.rankme.infrastructure"
])
class RankMe

fun main(args: Array<String>) {
    runApplication<RankMe>(*args)
}
