package com.tm.rankme.application.competitor

import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.game.Game

interface CompetitorService {
    fun getCompetitor(competitorId: String): Competitor
    fun getCompetitorForLeague(competitorId: String, leagueId: String): Competitor
    fun getCompetitors(leagueId: String): List<Competitor>
    fun saveCompetitor(competitor: Competitor): Competitor
    fun updateCompetitorsStatistic(firstCompetitor: Competitor, secondCompetitor: Competitor, game: Game)
}
