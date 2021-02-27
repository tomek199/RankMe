package com.tm.rankme

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer

@EnableConfigServer
@SpringBootApplication
class ConfigService

fun main(args: Array<String>) {
    runApplication<ConfigService>(*args)
}
