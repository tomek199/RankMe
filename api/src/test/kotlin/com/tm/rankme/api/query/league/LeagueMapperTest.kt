package com.tm.rankme.api.query.league

import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

internal class LeagueMapperTest {
    private val mapper = LeagueMapper()

    @Test
    internal fun `Should return league object from bytes`() {
        // given
        val bytes = """{"id":"54558379-defa-4f2d-9e08-0fd7689e18e8", "name":"Star Wars",
            "allowDraws":true, "maxScore":3}""".toByteArray()
        // when
        val league = mapper.deserialize(bytes)
        // then
        assertNotNull(league)
        assertEquals(UUID.fromString("54558379-defa-4f2d-9e08-0fd7689e18e8"), league.id)
        assertEquals("Star Wars", league.name)
        assertTrue(league.allowDraws)
        assertEquals(3, league.maxScore)
    }
}