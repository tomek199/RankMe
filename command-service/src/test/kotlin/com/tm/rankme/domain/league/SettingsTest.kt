package com.tm.rankme.domain.league

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test


internal class SettingsTest {
    @Test
    internal fun `Should create default settings`() {
        // when
        val settings = Settings()
        // then
        assertEquals(2, settings.maxScore)
        assertEquals(false, settings.allowDraws)
    }
}