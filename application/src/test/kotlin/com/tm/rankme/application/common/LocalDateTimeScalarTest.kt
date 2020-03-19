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


internal class LocalDateTimeScalarTest {
    private val scalar = LocalDateTimeScalar()

    @Test
    internal fun `Should convert String value to LocalDateTime`() {
        // given
        val expectedDate = LocalDateTime.of(2020, 6, 15, 13, 45, 34)
        // when
        val result = scalar.coercing.parseValue("2020-06-15T13:45:34")
        // then
        assertEquals(expectedDate, result)
    }

    @Test
    internal fun `Should throw exception when converting incorrect String value to LocalDateTime`() {
        // then
        assertFailsWith<CoercingParseValueException> { scalar.coercing.parseValue("2020-8-7T22:18:45") }
    }

    @Test
    internal fun `Should return null when converting null value to LocalDateTime`() {
        // when
        val result = scalar.coercing.parseValue(null)
        // then
        assertNull(result)
    }

    @Test
    internal fun `Should convert StringValue literal to LocalDateTime`() {
        // given
        val literal = StringValue("2020-12-04T06:07:54")
        // when
        val result = scalar.coercing.parseLiteral(literal)
        // then
        assertEquals(LocalDateTime.of(2020, 12, 4, 6, 7, 54), result)
    }

    @Test
    internal fun `Should throw exception when converting incorrect StringValue literal to LocalDateTime`() {
        // when
        val literal = StringValue("2020-3-2T17:3:8")
        // then
        assertFailsWith<CoercingParseLiteralException> { scalar.coercing.parseLiteral(literal) }
    }

    @Test
    internal fun `Should return null when converting null literal to LocalDateTime`() {
        // when
        val result = scalar.coercing.parseLiteral(null)
        // then
        assertNull(result)
    }

    @Test
    internal fun `Should serialize String value`() {
        // given
        val expectedValue = "2020-10-07T09:34:27"
        // when
        val result = scalar.coercing.serialize(expectedValue)
        // then
        assertEquals(expectedValue, result)
    }

    @Test
    internal fun `Should serialize LocalDateTime value`() {
        // given
        val expectedValue = "2020-06-07T13:51:27"
        // when
        val result = scalar.coercing.serialize(LocalDateTime.of(2020, 6, 7, 13, 51, 27))
        // then
        assertEquals(expectedValue, result)
    }

    @Test
    internal fun `Should serialize null value`() {
        // when
        val result = scalar.coercing.serialize(null)
        // then
        assertNull(result)
    }

    @Test
    internal fun `Should throw exception when serialize value is incorrect`() {
        // then
        assertFailsWith<CoercingSerializeException> { scalar.coercing.serialize("abcd1234") }
    }
}