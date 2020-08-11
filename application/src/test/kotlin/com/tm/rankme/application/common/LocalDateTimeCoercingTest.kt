package com.tm.rankme.application.common

import graphql.language.StringValue
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull


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
    internal fun `Should return null when converting null value to LocalDateTime`() {
        // when
        val result = coercing.parseValue(null)
        // then
        assertNull(result)
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
    internal fun `Should return null when converting null literal to LocalDateTime`() {
        // when
        val result = coercing.parseLiteral(null)
        // then
        assertNull(result)
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
    internal fun `Should serialize null value`() {
        // when
        val result = coercing.serialize(null)
        // then
        assertNull(result)
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