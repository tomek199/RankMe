package com.tm.rankme.application.game

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.domain.event.EventRepository
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameFactory
import com.tm.rankme.domain.game.GameRepository
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class GameMutation(
    private val gameRepository: GameRepository,
    private val eventRepository: EventRepository,
    private val competitorService: CompetitorService,
    @Qualifier("gameMapper") private val mapper: Mapper<Game, GameModel>
) : GraphQLMutationResolver {

    fun addGame(input: AddGameInput): GameModel {
        val firstCompetitor = competitorService.getCompetitor(input.playerOneId, input.leagueId)
        val secondCompetitor = competitorService.getCompetitor(input.playerTwoId, input.leagueId)
        val game = GameFactory.create(
            firstCompetitor, input.playerOneScore,
            secondCompetitor, input.playerTwoScore, input.leagueId
        )
        competitorService.updateCompetitorsStatistic(firstCompetitor, secondCompetitor, game)
        return mapper.toModel(gameRepository.save(game))
    }

    fun completeGame(input: CompleteGameInput): GameModel {
        val event = eventRepository.findById(input.eventId)
            ?: throw IllegalStateException("Event ${input.eventId} is not found")
        val firstCompetitor = competitorService.getCompetitor(event.memberOne.competitorId, event.leagueId)
        val secondCompetitor = competitorService.getCompetitor(event.memberTwo.competitorId, event.leagueId)
        val game = GameFactory.create(
            firstCompetitor, input.playerOneScore,
            secondCompetitor, input.playerTwoScore, event.leagueId
        )
        competitorService.updateCompetitorsStatistic(firstCompetitor, secondCompetitor, game)
        eventRepository.delete(input.eventId)
        return mapper.toModel(gameRepository.save(game))
    }
}
