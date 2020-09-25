package com.tm.rankme.application.match

import com.tm.rankme.domain.match.Match
import java.time.LocalDateTime

interface MatchService {
    fun get(matchId: String): Match
    fun create(
        leagueId: String,
        firstMemberId: String, secondMemberId: String,
        dateTime: LocalDateTime
    ): MatchModel

    fun remove(matchId: String)
}