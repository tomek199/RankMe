package com.tm.rankme.query

import com.tm.rankme.model.player.Player
import com.tm.rankme.model.player.PlayerRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class PlayerController(private val repository: PlayerRepository) {
    private val log = LoggerFactory.getLogger(PlayerController::class.java)

    @GetMapping("/players/{id}")
    fun player(@PathVariable id: String): Player? {
        log.info("Get player by id=$id")
        return repository.byId(UUID.fromString(id))
    }

    @GetMapping("/leagues/{leagueId}/players")
    fun playersByLeagueId(@PathVariable leagueId: String): List<Player> {
        log.info("Get players by leagueId=$leagueId")
        return repository.byLeagueId(UUID.fromString(leagueId))
    }
}