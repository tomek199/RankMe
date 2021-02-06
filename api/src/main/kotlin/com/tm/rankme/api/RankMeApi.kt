package com.tm.rankme.api

import com.tm.rankme.api.scalar.LocalDateTimeCoercing
import com.tm.rankme.api.scalar.UUIDCoercing
import graphql.schema.GraphQLScalarType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class RankMeApi {
    @Bean
    fun uuidScalar(): GraphQLScalarType {
        return GraphQLScalarType.newScalar()
            .name("UUID")
            .coercing(UUIDCoercing())
            .build()
    }

    @Bean
    fun localDateTimeScalar(): GraphQLScalarType {
        return GraphQLScalarType.newScalar()
            .name("LocalDateTime")
            .coercing(LocalDateTimeCoercing())
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<RankMeApi>(*args)
}
