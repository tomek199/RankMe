package com.tm.rankme.api

import com.tm.rankme.api.scalar.LocalDateTimeCoercing
import com.tm.rankme.api.scalar.UUIDCoercing
import graphql.schema.GraphQLScalarType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

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

    @Bean
    fun queryServiceRestTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder.build()
    }
}

fun main(args: Array<String>) {
    runApplication<RankMeApi>(*args)
}
