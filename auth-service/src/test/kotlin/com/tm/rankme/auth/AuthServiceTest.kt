package com.tm.rankme.auth

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = ["test"])
internal class AuthServiceTest {
    @Test
    internal fun `Should load context`() {
        // checking if application starts correctly
    }

    @Test
    internal fun `Should return RestTemplate instance`() {
        // when
        val restTemplate = AuthService().authRestTemplate(RestTemplateBuilder())
        // then
        assertNotNull(restTemplate)
    }
}
