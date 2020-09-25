package com.tm.rankme.application.match

import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.only
import org.mockito.Mockito.verify
import java.time.LocalDateTime
import kotlin.test.assertNotNull

internal class MatchMutationTest {
    private val matchService = Mockito.mock(MatchService::class.java)
    private val mutation = MatchMutation(matchService)
    private val leagueId = "league-1"

    @Test
    internal fun `Should add new match`() {
        // given
        val memberOneId = "comp-1"
        val memberOne = MemberModel(memberOneId, "Batman", 314, 1643)
        val memberTwoId = "comp-2"
        val memberTwo = MemberModel(memberTwoId, "Superman", 156, 2895)
        val matchDateTime = LocalDateTime.now()
        val expectedMatch = MatchModel("match-1", memberOne, memberTwo, matchDateTime)
        given(matchService.create(leagueId, memberOneId, memberTwoId, matchDateTime)).willReturn(expectedMatch)
        val input = AddMatchInput(leagueId, memberOneId, memberTwoId, matchDateTime)
        // when
        val match: MatchModel = mutation.addMatch(input)
        // then
        assertNotNull(match)
        verify(matchService, only()).create(leagueId, memberOneId, memberTwoId, matchDateTime)
    }
}