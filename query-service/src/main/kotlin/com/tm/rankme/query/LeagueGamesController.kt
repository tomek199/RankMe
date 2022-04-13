package com.tm.rankme.query

import com.tm.rankme.model.Page
import com.tm.rankme.model.game.Game
import com.tm.rankme.model.game.GameRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/leagues/{leagueId}")
class LeagueGamesController(private val repository: GameRepository) {
    private val log = LoggerFactory.getLogger(LeagueGamesController::class.java)

    @GetMapping("/games")
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

    @GetMapping("/completed-games")
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

    @GetMapping("/scheduled-games")
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
}