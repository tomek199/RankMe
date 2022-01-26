package com.tm.rankme.api.query.game

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class GameDeserializer : JsonDeserializer<Game>() {
    private val mapper = jacksonObjectMapper().also {
        it.registerModule(JavaTimeModule())
        it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    override fun deserialize(parser: JsonParser?, context: DeserializationContext?): Game {
        val gameNode = context?.readTree(parser) ?: throw IllegalArgumentException("Cannot parse game")
        return if (gameNode.hasNonNull("result")) mapper.treeToValue(gameNode, CompletedGame::class.java)
        else mapper.treeToValue(gameNode, ScheduledGame::class.java)
    }
}