package com.tm.rankme

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class DiscoveryService

fun main(args: Array<String>) {
    runApplication<DiscoveryService>(*args)
}
