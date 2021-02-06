package com.tm.rankme.api.query.player

import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

internal class PlayerMapperTest {
    private val mapper = PlayerMapper()

    @Test
    internal fun `Should return player object from bytes`() {
        // given
        val bytes = """{"id":"a48ebb54-a689-4d78-9291-bae381a6b362", "name":"Optimus Prime",
            "deviation":296, "rating":1834}""".toByteArray()
        // when
        val player = mapper.deserialize(bytes)
        // then
        assertNotNull(player)
        assertEquals(UUID.fromString("a48ebb54-a689-4d78-9291-bae381a6b362"), player.id)
        assertEquals("Optimus Prime", player.name)
        assertEquals(296, player.deviation)
        assertEquals(1834, player.rating)
    }
}