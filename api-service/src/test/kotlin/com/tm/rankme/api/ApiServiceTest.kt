package com.tm.rankme.api

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ApiServiceTest {
    @Test
    internal fun `Should load context`() {
        // checking if application starts correctly
    }

    @Test
    internal fun `Should return UUID scalar`() {
        // when
        val scalar = ApiService().uuidScalar()
        // then
        assertNotNull(scalar)
        assertEquals("UUID", scalar.name)
    }

    @Test
    internal fun `Should return LocalDateTime scalar`() {
        // when
        val scalar = ApiService().localDateTimeScalar()
        // then
        assertNotNull(scalar)
        assertEquals("LocalDateTime", scalar.name)
    }

    @Test
    internal fun `Should return RestTemplate instance`() {
        // when
        val restTemplate = ApiService().queryServiceRestTemplate(RestTemplateBuilder())
        // then
        assertNotNull(restTemplate)
    }
}