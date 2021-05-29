package com.tm.rankme.query

import com.tm.rankme.model.Page
import com.tm.rankme.model.game.Game
import com.tm.rankme.model.game.GameRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/games")
class GameController(private val repository: GameRepository) {
    private val log = LoggerFactory.getLogger(GameController::class.java)

    @GetMapping("")
    fun gamesByLeagueId(
        @RequestParam leagueId: String,
        @RequestParam first: Int,
        @RequestParam(required = false) after: String?
    ): Page<Game> {
        log.info("Get games by leagueId=$leagueId, first=$first, after=$after")
        return repository.byLeagueId(UUID.fromString(leagueId), first, after)
    }
}