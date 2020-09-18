package com.tm.rankme.application.competitor

import com.tm.rankme.domain.competitor.Competitor
import com.tm.rankme.domain.game.Game

interface CompetitorService {
    fun get(competitorId: String): CompetitorModel
    fun getForLeague(competitorId: String, leagueId: String): Competitor
    fun getListForLeague(leagueId: String): List<CompetitorModel>
    fun create(leagueId: String, username: String): CompetitorModel
    fun updateStatistic(firstCompetitor: Competitor, secondCompetitor: Competitor, game: Game)
}
