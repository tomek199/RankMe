package com.tm.rankme.application

import com.coxautodev.graphql.tools.PerFieldObjectMapperProvider
import com.coxautodev.graphql.tools.SchemaParserOptions
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan


@SpringBootApplication
@ComponentScan(value = ["com.tm.rankme.application", "com.tm.rankme.infrastructure"])
class Application {

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().registerModule(JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Bean
    fun schemaParserOptions(): SchemaParserOptions? {
        return SchemaParserOptions.newOptions()
                .objectMapperProvider(PerFieldObjectMapperProvider { objectMapper() })
                .build()
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
