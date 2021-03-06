package com.tm.rankme.query

import com.tm.rankme.model.player.Player
import com.tm.rankme.model.player.PlayerRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/players")
class PlayerController(private val repository: PlayerRepository) {
    private val log = LoggerFactory.getLogger(PlayerController::class.java)

    @GetMapping("/{id}")
    fun player(@PathVariable id: String): Player? = repository.byId(UUID.fromString(id)).also {
        log.info("Get player by id=$id")
    }
}