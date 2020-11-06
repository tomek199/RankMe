package com.tm.rankme.api.query

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.info.BuildProperties

internal class InfoQueryTest {
    private val buildProperties: BuildProperties = mock()
    private val query: InfoQuery = InfoQuery(buildProperties)

    @Test
    internal fun `Should return info message`() {
        // given
        val version = "1.2.3"
        given(buildProperties.version).willReturn(version)
        // when
        val result = query.info()
        // then
        assertEquals("RankMe GraphQL API $version", result)
    }
}
