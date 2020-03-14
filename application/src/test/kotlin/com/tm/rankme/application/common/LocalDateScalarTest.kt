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

internal class LocalDateScalarTest {
    private val scalar = LocalDateScalar()

    @Test
    internal fun `Should convert String value to LocalDate`() {
        // given
        val expectedDate = LocalDate.of(2020, 6, 15)
        // when
        val result = scalar.coercing.parseValue("2020-06-15")
        // then
        assertEquals(expectedDate, result)
    }

    @Test
    internal fun `Should throw exception when converting incorrect String value to LocalDate`() {
        // then
        assertFailsWith<CoercingParseValueException> { scalar.coercing.parseValue("2020-8-7") }
    }

    @Test
    internal fun `Should return null when converting null value to LocalDate`() {
        // when
        val result = scalar.coercing.parseValue(null)
        // then
        assertNull(result)
    }

    @Test
    internal fun `Should convert StringValue literal to LocalDate`() {
        // given
        val literal = StringValue("2020-12-04")
        // when
        val result = scalar.coercing.parseLiteral(literal)
        // then
        assertEquals(LocalDate.of(2020, 12, 4), result)
    }

    @Test
    internal fun `Should throw exception when converting incorrect StringValue literal to LocalDate`() {
        // when
        val literal = StringValue("2020-3-2")
        // then
        assertFailsWith<CoercingParseLiteralException> { scalar.coercing.parseLiteral(literal) }
    }

    @Test
    internal fun `Should return null when converting null literal to LocalDate`() {
        // when
        val result = scalar.coercing.parseLiteral(null)
        // then
        assertNull(result)
    }

    @Test
    internal fun `Should serialize String value`() {
        // given
        val expectedValue = "2020-10-07"
        // when
        val result = scalar.coercing.serialize(expectedValue)
        // then
        assertEquals(expectedValue, result)
    }

    @Test
    internal fun `Should serialize LocalDate value`() {
        // given
        val expectedValue = "2020-06-07"
        // when
        val result = scalar.coercing.serialize(LocalDate.of(2020, 6, 7))
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