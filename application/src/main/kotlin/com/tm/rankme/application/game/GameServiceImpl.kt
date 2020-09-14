package com.tm.rankme.application.game

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.application.event.EventService
import com.tm.rankme.domain.Side
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameFactory
import com.tm.rankme.domain.game.GameRepository
import org.springframework.stereotype.Service

@Service
internal class GameServiceImpl(
    private val gameRepository: GameRepository,
    private val competitorService: CompetitorService,
    private val eventService: EventService,
    private val mapper: Mapper<Game, GameModel>
) : GameService {

    override fun get(gameId: String): GameModel {
        val game = gameRepository.findById(gameId) ?: throw IllegalStateException("Game $gameId is not found")
        return mapper.toModel(game)
    }

    override fun create(
        leagueId: String,
        firstCompetitorId: String, firstScore: Int,
        secondCompetitorId: String, secondScore: Int
    ): GameModel {
        val firstCompetitor = competitorService.getForLeague(firstCompetitorId, leagueId)
        val secondCompetitor = competitorService.getForLeague(secondCompetitorId, leagueId)
        val game = GameFactory.create(firstCompetitor, firstScore, secondCompetitor, secondScore, leagueId)
        val createdGame = gameRepository.save(game)
        competitorService.updateStatistic(firstCompetitor, secondCompetitor, game)
        return mapper.toModel(createdGame)
    }

    override fun complete(eventId: String, playerOneScore: Int, playerTwoScore: Int): GameModel {
        val event = eventService.get(eventId)
        val firstCompetitor = competitorService.getForLeague(event.memberOne.competitorId, event.leagueId)
        val secondCompetitor = competitorService.getForLeague(event.memberTwo.competitorId, event.leagueId)
        val game = GameFactory.create(firstCompetitor, playerOneScore, secondCompetitor, playerTwoScore, event.leagueId)
        val createdGame = gameRepository.save(game)
        competitorService.updateStatistic(firstCompetitor, secondCompetitor, game)
        eventService.remove(eventId)
        return mapper.toModel(createdGame)
    }

    override fun getSideForLeague(leagueId: String, first: Int, after: String?): Side<Game> {
        return gameRepository.findByLeagueId(leagueId, first, after)
    }
}