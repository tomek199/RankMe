package com.tm.rankme

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class GatewayService

fun main(args: Array<String>) {
    runApplication<GatewayService>(*args)
}
