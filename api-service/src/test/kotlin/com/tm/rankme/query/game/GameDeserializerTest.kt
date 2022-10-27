package com.tm.rankme.query.game

import com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.random.Random.Default.nextInt
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class GameDeserializerTest {
    private val deserializer = GameDeserializer()
    private val parser = mockk<JsonParser>()
    private val context = mockk<DeserializationContext>()
    private val mapper = jacksonObjectMapper().also {
        it.registerModule(JavaTimeModule())
        it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @Test
    internal fun `Should deserialize completed game`() {
        // given
        val expectedGame = CompletedGame(
            randomNanoId(), LocalDateTime.now(),
            randomNanoId(), "Batman", nextInt(), nextInt(),
            randomNanoId(), "Superman", nextInt(), nextInt(),
            Result(nextInt(), nextInt(), nextInt(), nextInt(), nextInt(), nextInt())
        )
        val jsonNode = mapper.valueToTree<JsonNode>(expectedGame)
        every { context.readTree(parser) } returns jsonNode
        // when
        val game = deserializer.deserialize(parser, context)
        // then
        assertTrue(game is CompletedGame)
    }

    @Test
    internal fun `Should deserialize scheduled game`() {
        // given
        val expectedGame = ScheduledGame(
            randomNanoId(), LocalDateTime.now(),
            randomNanoId(), "Batman", nextInt(), nextInt(),
            randomNanoId(), "Superman", nextInt(), nextInt()
        )
        val jsonNode = mapper.valueToTree<JsonNode>(expectedGame)
        every { context.readTree(parser) } returns jsonNode
        // when
        val game = deserializer.deserialize(parser, context)
        // then
        assertTrue(game is ScheduledGame)
    }

    @Test
    internal fun `Should throw exception when deserialization context is null`() {
        // when
        val exception = assertThrows<IllegalArgumentException> { deserializer.deserialize(null, null) }
        // then
        assertEquals("Cannot parse game", exception.message)
    }
}