package com.tm.rankme.infrastructure.memory

import com.tm.rankme.domain.game.Game
import com.tm.rankme.domain.game.GameRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("dev")
class GameMemoryStorage : GameRepository {
    private val games: MutableList<Game> = mutableListOf()

    override fun save(entity: Game): Game {
        val id = (games.size + 1).toString()
        val game = Game(id, entity.playerOne, entity.playerTwo, entity.leagueId, entity.dateTime)
        games.add(game)
        return game
    }

    override fun findAll(): Collection<Game> {
        return games
    }

    override fun findById(id: String): Game? {
        return games.find { game -> game.id.equals(id) }
    }

    override fun delete(id: String) {
        games.removeIf { game -> game.id.equals(id) }
    }
}