package com.tm.rankme.infrastructure.game

import com.tm.rankme.infrastructure.decode
import com.tm.rankme.model.Page
import com.tm.rankme.model.game.Game
import com.tm.rankme.model.game.GameRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class MongoGameRepository(
    private val accessor: MongoGameAccessor
) : MongoGamesPage(), GameRepository {

    override fun byId(id: String): Game? = accessor.findByIdOrNull(id)?.let { gameFromEntity(it) }

    override fun store(game: Game) {
        val result = game.result?.let { gameResult ->
            Result(
                gameResult.playerOneScore, gameResult.playerOneDeviationDelta, gameResult.playerOneRatingDelta,
                gameResult.playerTwoScore, gameResult.playerTwoDeviationDelta, gameResult.playerTwoRatingDelta
            )
        }
        val entity = GameEntity(
            game.id, game.leagueId, game.dateTime,
            game.playerOneId, game.playerOneName, game.playerOneRating, game.playerOneDeviation,
            game.playerTwoId, game.playerTwoName, game.playerTwoRating, game.playerTwoDeviation,
            result
        )
        accessor.save(entity)
    }

    override fun byLeagueId(leagueId: String, first: Int): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByLeagueIdOrderByTimestampDesc(leagueId, pageable)
        val games = page.content.map(this::itemForEntity)
        return Page(games, false, page.hasNext())
    }

    override fun byLeagueIdAfter(leagueId: String, first: Int, after: String): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByLeagueIdAndTimestampLessThanOrderByTimestampDesc(leagueId, decode(after), pageable)
        val games = page.content.map(this::itemForEntity)
        return Page(games, true, page.hasNext())
    }

    override fun byLeagueIdBefore(leagueId: String, first: Int, before: String): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByLeagueIdAndTimestampGreaterThanOrderByTimestampAsc(leagueId, decode(before), pageable)
        val games = page.content.reversed().map(this::itemForEntity)
        return Page(games, page.hasNext(), true)
    }

    override fun completedByLeagueId(leagueId: String, first: Int): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByLeagueIdAndResultNotNullOrderByTimestampDesc(leagueId, pageable)
        val games = page.content.map(this::itemForEntity)
        return Page(games, false, page.hasNext())
    }

    override fun completedByLeagueIdAfter(leagueId: String, first: Int, after: String): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByLeagueIdAndTimestampLessThanAndResultNotNullOrderByTimestampDesc(leagueId, decode(after), pageable)
        val games = page.content.map(this::itemForEntity)
        return Page(games, true, page.hasNext())
    }

    override fun completedByLeagueIdBefore(leagueId: String, first: Int, before: String): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByLeagueIdAndTimestampGreaterThanAndResultNotNullOrderByTimestampAsc(leagueId, decode(before), pageable)
        val games = page.content.reversed().map(this::itemForEntity)
        return Page(games, page.hasNext(), true)
    }

    override fun scheduledByLeagueId(leagueId: String, first: Int): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByLeagueIdAndResultNullOrderByTimestampDesc(leagueId, pageable)
        val games = page.content.map(this::itemForEntity)
        return Page(games, false, page.hasNext())
    }

    override fun scheduledByLeagueIdAfter(leagueId: String, first: Int, after: String): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByLeagueIdAndTimestampLessThanAndResultNullOrderByTimestampDesc(leagueId, decode(after), pageable)
        val games = page.content.map(this::itemForEntity)
        return Page(games, true, page.hasNext())
    }

    override fun scheduledByLeagueIdBefore(leagueId: String, first: Int, before: String): Page<Game> {
        val pageable = PageRequest.of(0, first)
        val page = accessor.getByLeagueIdAndTimestampGreaterThanAndResultNullOrderByTimestampAsc(leagueId, decode(before), pageable)
        val games = page.content.reversed().map(this::itemForEntity)
        return Page(games, page.hasNext(), true)
    }
}