package com.tm.rankme.api.scalar

import graphql.language.StringValue
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class LocalDateTimeCoercingTest {
    private val coercing = LocalDateTimeCoercing()

    @Test
    internal fun `Should convert String value to LocalDateTime`() {
        // given
        val expectedDate = LocalDateTime.of(2020, 6, 15, 13, 45, 34)
        // when
        val result = coercing.parseValue("2020-06-15T13:45:34")
        // then
        assertEquals(expectedDate, result)
    }

    @Test
    internal fun `Should throw exception when converting incorrect String value to LocalDateTime`() {
        // given
        val input = "2020-8-7T22:18:45"
        // when
        val exception = assertFailsWith<CoercingParseValueException> { coercing.parseValue(input) }
        // then
        assertEquals("Could not parse value $input for LocalDateTime", exception.message)
    }

    @Test
    internal fun `Should throw exception when converting no String value to LocalDateTime`() {
        // given
        val input = Int.MAX_VALUE
        // when
        val exception = assertFailsWith<CoercingParseValueException> { coercing.parseValue(input) }
        // then
        assertEquals("Incorrect value type ${input::class} for LocalDateTime", exception.message)
    }

    @Test
    internal fun `Should convert StringValue literal to LocalDateTime`() {
        // given
        val literal = StringValue("2020-12-04T06:07:54")
        // when
        val result = coercing.parseLiteral(literal)
        // then
        assertEquals(LocalDateTime.of(2020, 12, 4, 6, 7, 54), result)
    }

    @Test
    internal fun `Should throw exception when converting incorrect StringValue literal to LocalDateTime`() {
        // given
        val literal = StringValue("2020-3-2T17:3:8")
        // when
        val exception = assertFailsWith<CoercingParseLiteralException> { coercing.parseLiteral(literal) }
        // then
        assertEquals("Could not parse literal $literal for LocalDateTime", exception.message)
    }

    @Test
    internal fun `Should throw exception when converting no String literal to LocalDateTime`() {
        // given
        val input = Int.MIN_VALUE
        // when
        val exception = assertFailsWith<CoercingParseLiteralException> { coercing.parseLiteral(input) }
        // then
        assertEquals("Incorrect literal type ${input::class} for LocalDateTime", exception.message)
    }

    @Test
    internal fun `Should serialize String value`() {
        // given
        val expectedValue = "2020-10-07T09:34:27"
        // when
        val result = coercing.serialize(expectedValue)
        // then
        assertEquals(expectedValue, result)
    }

    @Test
    internal fun `Should serialize LocalDateTime value`() {
        // given
        val expectedValue = "2020-06-07T13:51:27"
        // when
        val result = coercing.serialize(LocalDateTime.of(2020, 6, 7, 13, 51, 27))
        // then
        assertEquals(expectedValue, result)
    }

    @Test
    internal fun `Should throw exception when serialize type is incorrect`() {
        // given
        val input = Int.MAX_VALUE
        // when
        val exception = assertFailsWith<CoercingSerializeException> { coercing.serialize(input) }
        // then
        assertEquals("Incorrect type to serialize ${input::class}", exception.message)
    }

    @Test
    internal fun `Should throw exception when serialize value is incorrect`() {
        // given
        val input = "abcd1234"
        // when
        val exception = assertFailsWith<CoercingSerializeException> { coercing.serialize(input) }
        // then
        assertEquals("Could not serialize object $input", exception.message)
    }
}