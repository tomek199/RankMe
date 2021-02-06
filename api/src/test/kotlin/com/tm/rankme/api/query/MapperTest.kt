package com.tm.rankme.api.query

import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

internal class MapperTest {
    private val mapper = object : Mapper<Any>() {
        override fun deserialize(data: ByteArray): Any = Any()
        fun objectMapper() = objectMapper
    }

    @Test
    internal fun `Should return object mapper`() {
        // when
        val objectMapper = mapper.objectMapper()
        // then
        assertNotNull(objectMapper)
    }
}