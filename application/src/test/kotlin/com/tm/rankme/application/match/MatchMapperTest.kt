package com.tm.rankme.application.match

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.domain.match.Match
import com.tm.rankme.domain.match.Member
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class MatchMapperTest {
    private val mapper: Mapper<Match, MatchModel> = MatchMapper()
    private val memberOne = Member("member-1", "Batman", 253, 1568)
    private val memberTwo = Member("member-2", "Superman", 187, 2468)

    @Test
    internal fun `Should map match domain to model`() {
        // given
        val id = "match-1"
        val leagueId = "league-1"
        val dateTime = LocalDateTime.now()
        val domain = Match(id, leagueId, memberOne, memberTwo, dateTime)
        // when
        val model = mapper.toModel(domain)
        // then
        assertEquals(id, model.id)
        assertEquals(memberOne.competitorId, model.memberOne.competitorId)
        assertEquals(memberOne.username, model.memberOne.username)
        assertEquals(memberOne.deviation, model.memberOne.deviation)
        assertEquals(memberOne.rating, model.memberOne.rating)
        assertEquals(memberTwo.competitorId, model.memberTwo.competitorId)
        assertEquals(memberTwo.username, model.memberTwo.username)
        assertEquals(memberTwo.deviation, model.memberTwo.deviation)
        assertEquals(memberTwo.rating, model.memberTwo.rating)
        assertEquals(dateTime, model.dateTime)
    }

    @Test
    internal fun `Should throw IllegalStateException when domain match id is null`() {
        // given
        val domain = Match("match-1", memberOne, memberTwo, LocalDateTime.now())
        // when
        val exception = assertFailsWith<IllegalStateException> { mapper.toModel(domain) }
        // then
        assertEquals("Match id can't be null!", exception.message)
    }
}