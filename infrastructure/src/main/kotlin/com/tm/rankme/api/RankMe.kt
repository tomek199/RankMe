package com.tm.rankme.api

import com.tm.rankme.api.scalar.LocalDateTimeCoercing
import com.tm.rankme.api.scalar.UUIDCoercing
import graphql.schema.GraphQLScalarType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@ComponentScan(value = [
    "com.tm.rankme.api",
    "com.tm.rankme.cqrs.command",
    "com.tm.rankme.cqrs.query",
    "com.tm.rankme.storage"
])
@EnableMongoRepositories(value = [
    "com.tm.rankme.storage.read"
])
class RankMe {
    @Bean
    fun uuidScalar(): GraphQLScalarType {
        return GraphQLScalarType.newScalar()
            .name("UUID")
            .coercing(UUIDCoercing())
            .build()
    }

    fun localDateTimeScalar(): GraphQLScalarType {
        return GraphQLScalarType.newScalar()
            .name("LocalDateTime")
            .coercing(LocalDateTimeCoercing())
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<RankMe>(*args)
}
