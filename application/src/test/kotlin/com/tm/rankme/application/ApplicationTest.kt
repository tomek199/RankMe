package com.tm.rankme.application

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ApplicationTests {

    @Test
    internal fun `Should load context`() {
        // checking if application starts correctly
    }

    @Test
    internal fun `Should return ObjectMapper`() {
        // when
        val objectMapper = Application().objectMapper()
        // then
        assertNotNull(objectMapper)
    }

    @Test
    internal fun `Should return SchemaParserOptions`() {
        // when
        val schemaParserOptions = Application().schemaParserOptions()
        // then
        assertNotNull(schemaParserOptions)
    }
}
