package com.tm.rankme.application.game

import com.tm.rankme.application.common.Mapper
import com.tm.rankme.application.competitor.CompetitorService
import com.tm.rankme.application.match.MatchService
import com.tm.rankme.domain.Side
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameFactory
import com.tm.rankme.domain.game.GameRepository
import graphql.relay.Connection
import graphql.relay.DefaultConnection
import graphql.relay.DefaultConnectionCursor
import graphql.relay.DefaultEdge
import graphql.relay.DefaultPageInfo
import graphql.relay.SimpleListConnection
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Service
import java.util.*

@Service
internal class GameServiceImpl(
    private val gameRepository: GameRepository,
    private val competitorService: CompetitorService,
    private val matchService: MatchService,
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
        val createdGame = addNewGame(leagueId, firstCompetitorId, firstScore, secondCompetitorId, secondScore)
        return mapper.toModel(createdGame)
    }

    override fun complete(matchId: String, playerOneScore: Int, playerTwoScore: Int): GameModel {
        val match = matchService.getScheduled(matchId)
        val createdGame = addNewGame(
            match.leagueId,
            match.memberOne.competitorId, playerOneScore,
            match.memberTwo.competitorId, playerTwoScore
        )
        matchService.complete(matchId, createdGame.id!!)
        return mapper.toModel(createdGame)
    }

    private fun addNewGame(
        leagueId: String,
        firstCompetitorId: String, firstScore: Int,
        secondCompetitorId: String, secondScore: Int
    ): Game {
        val firstCompetitor = competitorService.getForLeague(firstCompetitorId, leagueId)
        val secondCompetitor = competitorService.getForLeague(secondCompetitorId, leagueId)
        val game = GameFactory.create(firstCompetitor, firstScore, secondCompetitor, secondScore, leagueId)
        val createdGame = gameRepository.save(game)
        competitorService.updateStatistic(firstCompetitor, secondCompetitor, game)
        return createdGame
    }

    override fun getConnectionForLeague(
        leagueId: String, first: Int,
        after: String?, env: DataFetchingEnvironment
    ): Connection<GameModel> {
        val decodedAfter = if (after != null) String(Base64.getDecoder().decode(after)) else null
        val side = gameRepository.findByLeagueId(leagueId, first, decodedAfter)
        if (side.content.isEmpty()) return SimpleListConnection<GameModel>(emptyList()).get(env)
        return DefaultConnection(createEdges(side), createPageInfo(side))
    }

    private fun createEdges(side: Side<Game>): List<DefaultEdge<GameModel>> {
        return side.content.map { game ->
            val cursor = DefaultConnectionCursor(encode(game.id))
            DefaultEdge(mapper.toModel(game), cursor)
        }
    }

    private fun createPageInfo(side: Side<Game>): DefaultPageInfo {
        val startCursor = encode(side.content.first().id)
        val endCursor = encode(side.content.last().id)
        return DefaultPageInfo(
            DefaultConnectionCursor(startCursor), DefaultConnectionCursor(endCursor),
            side.hasPrevious, side.hasNext
        )
    }

    private fun encode(value: String?): String {
        value ?: throw IllegalStateException("Value to encode is null")
        return Base64.getEncoder().encodeToString(value.toByteArray())
    }
}
