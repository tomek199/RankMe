package com.tm.rankme.application.game

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.domain.event.EventRepository
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameFactory
import com.tm.rankme.domain.game.GameRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class GameMutation(
    private val gameRepository: GameRepository,
    private val eventRepository: EventRepository,
    private val competitorService: CompetitorService,
    @Qualifier("gameMapper") private val mapper: Mapper<Game, GameModel>
) : GraphQLMutationResolver {

    fun addGame(
        leagueId: String, playerOneId: String, playerOneScore: Int,
        playerTwoId: String, playerTwoScore: Int
    ): GameModel {
        val firstCompetitor = competitorService.getCompetitor(playerOneId, leagueId)
        val secondCompetitor = competitorService.getCompetitor(playerTwoId, leagueId)
        val game = GameFactory.create(
            firstCompetitor, playerOneScore,
            secondCompetitor, playerTwoScore, leagueId
        )
        competitorService.updateCompetitorsStatistic(firstCompetitor, secondCompetitor, game)
        return mapper.toModel(gameRepository.save(game))
    }

    fun completeGame(eventId: String, playerOneScore: Int, playerTwoScore: Int): GameModel {
        val event = eventRepository.findById(eventId) ?: throw IllegalStateException("Event $eventId is not found")
        val firstCompetitor = competitorService.getCompetitor(event.memberOne.competitorId, event.leagueId)
        val secondCompetitor = competitorService.getCompetitor(event.memberTwo.competitorId, event.leagueId)
        val game = GameFactory.create(firstCompetitor, playerOneScore, secondCompetitor, playerTwoScore, event.leagueId)
        competitorService.updateCompetitorsStatistic(firstCompetitor, secondCompetitor, game)
        eventRepository.delete(eventId)
        return mapper.toModel(gameRepository.save(game))
    }
}