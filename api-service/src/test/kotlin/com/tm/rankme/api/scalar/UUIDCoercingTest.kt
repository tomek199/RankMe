package com.tm.rankme.api.scalar

import graphql.language.StringValue
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import org.junit.jupiter.api.Test

internal class UUIDCoercingTest {
    private val coercing = UUIDCoercing()

    @Test
    internal fun `Should convert String value to UUID`() {
        // given
        val uuid = UUID.randomUUID()
        // when
        val result = coercing.parseValue(uuid.toString())
        // then
        assertEquals(uuid, result)
    }

    @Test
    internal fun `Should throw exception when converting incorrect String value to UUID`() {
        // when
        val input = "1234abcd"
        val exception = assertFailsWith<CoercingParseValueException> { coercing.parseValue(input) }
        // then
        assertEquals("Could not parse value $input for UUID", exception.message)
    }

    @Test
    internal fun `Should return null when converting null value to UUID`() {
        // when
        val result = coercing.parseValue(null)
        // then
        assertNull(result)
    }

    @Test
    internal fun `Should convert StringValue literal to UUID`() {
        // given
        val uuid = UUID.randomUUID()
        val literal = StringValue(uuid.toString())
        // when
        val result = coercing.parseLiteral(literal)
        // then
        assertEquals(uuid, result)
    }

    @Test
    internal fun `Should throw exception when converting incorrect StringValue literal to UUID`() {
        // given
        val literal = StringValue("1234abcd")
        // when
        val exception = assertFailsWith<CoercingParseLiteralException> { coercing.parseLiteral(literal) }
        // then
        assertEquals("Could not parse literal $literal for UUID", exception.message)
    }

    @Test
    internal fun `Should return null when converting null literal to UUID`() {
        // when
        val result = coercing.parseLiteral(null)
        // then
        assertNull(result)
    }

    @Test
    internal fun `Should serialize String value`() {
        // given
        val uuid = UUID.randomUUID()
        // when
        val result = coercing.serialize(uuid.toString())
        // then
        assertEquals(uuid.toString(), result)
    }

    @Test
    internal fun `Should serialize UUID value`() {
        // given
        val uuid = UUID.randomUUID()
        // when
        val result = coercing.serialize(uuid)
        // then
        assertEquals(uuid.toString(), result)
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
        val input = "1234abcd"
        // when
        val exception = assertFailsWith<CoercingSerializeException> { coercing.serialize(input) }
        // then
        assertEquals("Could not serialize object $input", exception.message)
    }
}