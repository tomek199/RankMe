package com.tm.rankme.api

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class RankMeTest {
    @Test
    internal fun `Should load context`() {
        // checking if application starts correctly
    }

    @Test
    internal fun `Should return LocalDate scalar`() {
        // when
        val scalar = RankMe().uuidScalar()
        // then
        assertNotNull(scalar)
        assertEquals("UUID", scalar.name)
    }
}