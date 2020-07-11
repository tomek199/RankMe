package com.tm.rankme.domain.event

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class MemberTest {
    @Test
    internal fun `Should create member`() {
        // given
        val id = "member-1"
        val username = "Spiderman"
        val deviation = 234
        val rating = 1483
        // when
        val member = Member(id, username, deviation, rating)
        // then
        assertEquals(id, member.competitorId)
        assertEquals(username, member.username)
        assertEquals(deviation, member.deviation)
        assertEquals(rating, member.rating)
    }
}
