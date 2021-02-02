package com.tm.rankme.api

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class RankMeApiTest {
    @Test
    internal fun `Should load context`() {
        // checking if application starts correctly
    }

    @Test
    internal fun `Should return UUID scalar`() {
        // when
        val scalar = RankMeApi().uuidScalar()
        // then
        assertNotNull(scalar)
        assertEquals("UUID", scalar.name)
    }

    @Test
    internal fun `Should return LocalDateTime scalar`() {
        // when
        val scalar = RankMeApi().localDateTimeScalar()
        // then
        assertNotNull(scalar)
        assertEquals("LocalDateTime", scalar.name)
    }
}