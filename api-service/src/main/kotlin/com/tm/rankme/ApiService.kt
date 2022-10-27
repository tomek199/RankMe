package com.tm.rankme

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.module.SimpleModule
import com.tm.rankme.query.game.CompletedGame
import com.tm.rankme.query.game.Game
import com.tm.rankme.query.game.GameDeserializer
import com.tm.rankme.query.game.ScheduledGame
import com.tm.rankme.scalar.LocalDateTimeCoercing
import graphql.kickstart.tools.SchemaParserDictionary
import graphql.schema.GraphQLScalarType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

@SpringBootApplication
@EnableDiscoveryClient
class ApiService {
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

    @Bean
    fun schemaParserDictionary(): SchemaParserDictionary {
        return SchemaParserDictionary()
            .add(CompletedGame::class)
            .add(ScheduledGame::class)
    }

    @Bean
    fun objectMapperGameModule(): Module {
        val gameModule = SimpleModule()
        gameModule.addDeserializer(Game::class.java, GameDeserializer())
        return gameModule
    }
}

fun main(args: Array<String>) {
    runApplication<ApiService>(*args)
}
