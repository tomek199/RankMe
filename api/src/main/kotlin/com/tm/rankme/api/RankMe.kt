package com.tm.rankme.api

import com.tm.rankme.api.scalar.UUIDCoercing
import graphql.schema.GraphQLScalarType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(value = [
    "com.tm.rankme.api",
    "com.tm.rankme.application",
    "com.tm.rankme.infrastructure"
])
class RankMe {
    @Bean
    fun uuidScalar(): GraphQLScalarType {
        return GraphQLScalarType.newScalar()
            .name("UUID")
            .coercing(UUIDCoercing())
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<RankMe>(*args)
}
