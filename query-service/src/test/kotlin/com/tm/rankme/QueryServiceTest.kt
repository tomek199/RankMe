package com.tm.rankme

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = ["test"])
internal class QueryServiceTest {
    @Test
    internal fun `Should load context`() {
        // checking if application starts correctly
    }
}