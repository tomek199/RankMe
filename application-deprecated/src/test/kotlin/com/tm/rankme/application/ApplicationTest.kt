package com.tm.rankme.application

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ApplicationTests {
    @Test
    internal fun `Should load context`() {
        // checking if application starts correctly
    }

    @Test
    internal fun `Should return LocalDate scalar`() {
        // when
        val localDateScalar = Application().localDateScalar()
        // then
        assertNotNull(localDateScalar)
        assertEquals("LocalDate", localDateScalar.name)
    }

    @Test
    internal fun `Should return LocalDateTime scalar`() {
        // when
        val localDateTimeScalar = Application().localDateTimeScalar()
        // then
        assertNotNull(localDateTimeScalar)
        assertEquals("LocalDateTime", localDateTimeScalar.name)
    }
}
