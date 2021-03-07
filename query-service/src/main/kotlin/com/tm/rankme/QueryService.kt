package com.tm.rankme

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class QueryService

fun main(args: Array<String>) {
    runApplication<QueryService>(*args)
}
