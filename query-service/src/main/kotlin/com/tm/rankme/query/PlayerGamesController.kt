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
        @RequestParam(required = false) after: String?,
        @RequestParam(required = false) before: String?
    ): Page<Game> {
        log.info("Get games by playerId=$playerId, first=$first, after=$after, before=$before")
        return if (after != null) repository.byPlayerIdAfter(playerId, first, after)
            else if (before != null) repository.byPlayerIdBefore(playerId, first, before)
            else repository.byPlayerId(playerId, first)
    }

    @GetMapping("/completed-games")
    fun completedGamesByPlayerId(
        @PathVariable playerId: String,
        @RequestParam first: Int,
        @RequestParam(required = false) after: String?,
        @RequestParam(required = false) before: String?
    ): Page<Game> {
        log.info("Get completed games by playerId=$playerId, first=$first, after=$after, before=$before")
        return if (after != null) repository.completedByPlayerIdAfter(playerId, first, after)
            else if (before != null) repository.completedByPlayerIdBefore(playerId, first, before)
            else repository.completedByPlayerId(playerId, first)
    }

    @GetMapping("/scheduled-games")
    fun scheduledGamesByPlayerId(
        @PathVariable playerId: String,
        @RequestParam first: Int,
        @RequestParam(required = false) after: String?,
        @RequestParam(required = false) before: String?
    ): Page<Game> {
        log.info("Get scheduled games by playerId=$playerId, first=$first, after=$after, before=$before")
        return if (after != null) repository.scheduledByPlayerIdAfter(playerId, first, after)
            else if (before != null) repository.scheduledByPlayerIdBefore(playerId, first, before)
            else repository.scheduledByPlayerId(playerId, first)
    }
}