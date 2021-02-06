package com.tm.rankme

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CommandService

fun main(args: Array<String>) {
    runApplication<CommandService>(*args)
}
