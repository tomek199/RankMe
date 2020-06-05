package com.tm.rankme.domain

import com.tm.rankme.domain.league.League
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class SideTest {
    @Test
    internal fun `Should create Side object`() {
        // when
        val side = Side<League>(emptyList(), 0, hasPrevious = false, hasNext = false)
        // then
        assertEquals(0, side.total)
        assertTrue(side.content.isEmpty())
        assertFalse(side.hasPrevious)
        assertFalse(side.hasNext)
    }
}