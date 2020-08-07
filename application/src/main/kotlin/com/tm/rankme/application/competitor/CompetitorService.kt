package com.tm.rankme.application.competitor

import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.game.Game

interface CompetitorService {
    fun get(competitorId: String): Competitor
    fun getForLeague(competitorId: String, leagueId: String): Competitor
    fun getListForLeague(leagueId: String): List<Competitor>
    fun create(leagueId: String, username: String): Competitor
    fun updateStatistic(firstCompetitor: Competitor, secondCompetitor: Competitor, game: Game)
}
