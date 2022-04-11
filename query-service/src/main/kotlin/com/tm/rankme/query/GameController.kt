package com.tm.rankme.query

import com.tm.rankme.model.Page
import com.tm.rankme.model.game.Game
import com.tm.rankme.model.game.GameRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class GameController(private val repository: GameRepository) {
    private val log = LoggerFactory.getLogger(GameController::class.java)

    @GetMapping("/leagues/{leagueId}/games")
    fun gamesByLeagueId(
        @PathVariable leagueId: String,
        @RequestParam first: Int,
        @RequestParam(required = false) after: String?,
        @RequestParam(required = false) before: String?
    ): Page<Game> {
        log.info("Get games by leagueId=$leagueId, first=$first, after=$after")
        return if (after != null) repository.byLeagueIdAfter(leagueId, first, after)
            else if (before != null) repository.byLeagueIdBefore(leagueId, first, before)
            else repository.byLeagueId(leagueId, first)
    }

    @GetMapping("/leagues/{leagueId}/completed-games")
    fun completedGamesByLeagueId(
        @PathVariable leagueId: String,
        @RequestParam first: Int,
        @RequestParam(required = false) after: String?,
        @RequestParam(required = false) before: String?
    ): Page<Game> {
        log.info("Get completed games by leagueId=$leagueId, first=$first, after=$after")
        return if (after != null) repository.completedByLeagueIdAfter(leagueId, first, after)
            else if (before != null) repository.completedByLeagueIdBefore(leagueId, first, before)
            else repository.completedByLeagueId(leagueId, first)
    }

    @GetMapping("/leagues/{leagueId}/scheduled-games")
    fun scheduledGamesByLeagueId(
        @PathVariable leagueId: String,
        @RequestParam first: Int,
        @RequestParam(required = false) after: String?,
        @RequestParam(required = false) before: String?
    ): Page<Game> {
        log.info("Get scheduled games by leagueId=$leagueId, first=$first, after=$after")
        return if (after != null) repository.scheduledByLeagueIdAfter(leagueId, first, after)
            else if (before != null) repository.scheduledByLeagueIdBefore(leagueId, first, before)
            else repository.scheduledByLeagueId(leagueId, first)
    }

    @GetMapping("/players/{playerId}/games")
    fun gamesByPlayerId(
        @PathVariable playerId: String,
        @RequestParam first: Int,
        @RequestParam(required = false) after: String?
    ): Page<Game> {
        log.info("Get games by playerId=$playerId, first=$first, after=$after")
        return repository.byPlayerId(playerId, first, after)
    }

    @GetMapping("/players/{playerId}/completed-games")
    fun completedGamesByPlayerId(
        @PathVariable playerId: String,
        @RequestParam first: Int,
        @RequestParam(required = false) after: String?
    ): Page<Game> {
        log.info("Get completed games by playerId=$playerId, first=$first, after=$after")
        return repository.completedByPlayerId(playerId, first, after)
    }

    @GetMapping("/players/{playerId}/scheduled-games")
    fun scheduledGamesByPlayerId(
        @PathVariable playerId: String,
        @RequestParam first: Int,
        @RequestParam(required = false) after: String?
    ): Page<Game> {
        log.info("Get scheduled games by playerId=$playerId, first=$first, after=$after")
        return repository.scheduledByPlayerId(playerId, first, after)
    }
}