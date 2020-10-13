package com.tm.rankme.application.common

import graphql.language.StringValue
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

internal class LocalDateCoercingTest {
    private val coercing = LocalDateCoercing()

    @Test
    internal fun `Should convert String value to LocalDate`() {
        // given
        val expectedDate = LocalDate.of(2020, 6, 15)
        // when
        val result = coercing.parseValue("2020-06-15")
        // then
        assertEquals(expectedDate, result)
    }

    @Test
    internal fun `Should throw exception when converting incorrect String value to LocalDate`() {
        // when
        val input = "2020-8-7"
        val exception = assertFailsWith<CoercingParseValueException> { coercing.parseValue(input) }
        // then
        assertEquals("Could not parse value $input for LocalDate", exception.message)
    }

    @Test
    internal fun `Should return null when converting null value to LocalDate`() {
        // when
        val result = coercing.parseValue(null)
        // then
        assertNull(result)
    }

    @Test
    internal fun `Should convert StringValue literal to LocalDate`() {
        // given
        val literal = StringValue("2020-12-04")
        // when
        val result = coercing.parseLiteral(literal)
        // then
        assertEquals(LocalDate.of(2020, 12, 4), result)
    }

    @Test
    internal fun `Should throw exception when converting incorrect StringValue literal to LocalDate`() {
        // given
        val literal = StringValue("2020-3-2")
        // when
        val exception = assertFailsWith<CoercingParseLiteralException> { coercing.parseLiteral(literal) }
        // then
        assertEquals("Could not parse literal $literal for LocalDate", exception.message)
    }

    @Test
    internal fun `Should return null when converting null literal to LocalDate`() {
        // when
        val result = coercing.parseLiteral(null)
        // then
        assertNull(result)
    }

    @Test
    internal fun `Should serialize String value`() {
        // given
        val expectedValue = "2020-10-07"
        // when
        val result = coercing.serialize(expectedValue)
        // then
        assertEquals(expectedValue, result)
    }

    @Test
    internal fun `Should serialize LocalDate value`() {
        // given
        val expectedValue = "2020-06-07"
        // when
        val result = coercing.serialize(LocalDate.of(2020, 6, 7))
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