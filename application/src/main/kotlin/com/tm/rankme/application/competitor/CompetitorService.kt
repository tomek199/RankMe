package com.tm.rankme.application.competitor

import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.game.Game

interface CompetitorService {
    fun getCompetitor(id: String, leagueId: String): Competitor
    fun getByLeagueId(leagueId: String): List<Competitor>
    fun updateCompetitorsStatistic(firstCompetitor: Competitor, secondCompetitor: Competitor, game: Game)
}
