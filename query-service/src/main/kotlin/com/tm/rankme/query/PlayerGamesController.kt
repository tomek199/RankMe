package com.tm.rankme.query

import com.tm.rankme.model.Page
import com.tm.rankme.model.game.Game
import com.tm.rankme.model.game.PlayerGamesRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/players/{playerId}")
class PlayerGamesController(private val repository: PlayerGamesRepository) {
    private val log = LoggerFactory.getLogger(PlayerGamesController::class.java)

    @GetMapping("/games")
    fun gamesByPlayerId(
        @PathVariable playerId: String,
        @RequestParam first: Int,
        @RequestParam(required = false) after: String?
    ): Page<Game> {
        log.info("Get games by playerId=$playerId, first=$first, after=$after")
        return repository.byPlayerId(playerId, first, after)
    }

    @GetMapping("/completed-games")
    fun completedGamesByPlayerId(
        @PathVariable playerId: String,
        @RequestParam first: Int,
        @RequestParam(required = false) after: String?
    ): Page<Game> {
        log.info("Get completed games by playerId=$playerId, first=$first, after=$after")
        return repository.completedByPlayerId(playerId, first, after)
    }

    @GetMapping("/scheduled-games")
    fun scheduledGamesByPlayerId(
        @PathVariable playerId: String,
        @RequestParam first: Int,
        @RequestParam(required = false) after: String?
    ): Page<Game> {
        log.info("Get scheduled games by playerId=$playerId, first=$first, after=$after")
        return repository.scheduledByPlayerId(playerId, first, after)
    }
}