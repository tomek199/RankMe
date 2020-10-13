package com.tm.rankme.infrastructure.memory

import com.tm.rankme.domain.Side
import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("dev")
class GameMemoryStorage : GameRepository {
    private val games: MutableList<Game> = mutableListOf()

    override fun save(entity: Game): Game {
        if (entity.id == null) {
            val id = (games.size + 1).toString()
            val game = Game(id, entity.playerOne, entity.playerTwo, entity.leagueId, entity.dateTime, entity.type)
            games.add(game)
            return game
        }
        return entity
    }

    override fun findById(id: String): Game? {
        return games.find { game -> game.id.equals(id) }
    }

    override fun delete(id: String) {
        games.removeIf { game -> game.id.equals(id) }
    }

    override fun findByLeagueId(leagueId: String, first: Int, after: String?): Side<Game> {
        val gamesByLeague = games.filter { game -> game.leagueId == leagueId }
        val filteredGames =
            if (after != null) gamesByLeague.filter { game -> game.id!!.toInt() > after.toInt() }.take(first)
            else gamesByLeague.take(first)
        val hasPrevious = filteredGames.isNotEmpty() && gamesByLeague.first() != filteredGames.first()
        val hasNext = filteredGames.isNotEmpty() && gamesByLeague.last() != filteredGames.last()
        return Side(filteredGames, gamesByLeague.size, hasPrevious, hasNext)
    }
}