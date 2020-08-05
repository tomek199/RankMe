package com.tm.rankme.application.game

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.application.event.EventService
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameFactory
import com.tm.rankme.domain.game.GameRepository
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class GameMutation(
    private val gameRepository: GameRepository,
    private val eventService: EventService,
    private val competitorService: CompetitorService,
    @Qualifier("gameMapper") private val mapper: Mapper<Game, GameModel>
) : GraphQLMutationResolver {

    fun addGame(input: AddGameInput): GameModel {
        val firstCompetitor = competitorService.getForLeague(input.playerOneId, input.leagueId)
        val secondCompetitor = competitorService.getForLeague(input.playerTwoId, input.leagueId)
        val game = GameFactory.create(
            firstCompetitor, input.playerOneScore,
            secondCompetitor, input.playerTwoScore, input.leagueId
        )
        competitorService.updateStatistic(firstCompetitor, secondCompetitor, game)
        return mapper.toModel(gameRepository.save(game))
    }

    fun completeGame(input: CompleteGameInput): GameModel {
        val event = eventService.get(input.eventId)
        val firstCompetitor = competitorService.getForLeague(event.memberOne.competitorId, event.leagueId)
        val secondCompetitor = competitorService.getForLeague(event.memberTwo.competitorId, event.leagueId)
        val game = GameFactory.create(
            firstCompetitor, input.playerOneScore,
            secondCompetitor, input.playerTwoScore, event.leagueId
        )
        competitorService.updateStatistic(firstCompetitor, secondCompetitor, game)
        eventService.remove(input.eventId)
        return mapper.toModel(gameRepository.save(game))
    }
}
