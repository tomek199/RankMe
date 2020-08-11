package com.tm.rankme.application

import com.tm.rankme.application.common.LocalDateCoercing
import graphql.schema.GraphQLScalarType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan


@SpringBootApplication
@ComponentScan(value = ["com.tm.rankme.application", "com.tm.rankme.infrastructure"])
class Application {

    @Bean
    fun localDateScalar(): GraphQLScalarType {
        return GraphQLScalarType.newScalar()
            .name("LocalDate")
            .coercing(LocalDateCoercing())
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
