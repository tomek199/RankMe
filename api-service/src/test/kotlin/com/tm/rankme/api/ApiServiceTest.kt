package com.tm.rankme.api

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = ["test"])
internal class ApiServiceTest {
    @Test
    internal fun `Should load context`() {
        // checking if application starts correctly
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

    @Test
    internal fun `Should return schema parser dictionary`() {
        // when
        val dictionary = ApiService().schemaParserDictionary()
        // then
        assertEquals(setOf("CompletedGame", "ScheduledGame"), dictionary.getDictionary().keys)
    }

    @Test
    internal fun `Should return object mapper module for game deserialization`() {
        // when
        val module = ApiService().objectMapperGameModule()
        // then
        assertContains(module.moduleName, "SimpleModule")
    }
}