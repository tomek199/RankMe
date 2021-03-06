package com.tm.rankme.query

import com.tm.rankme.model.player.Player
import com.tm.rankme.model.player.PlayerRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/players")
class PlayerController(private val repository: PlayerRepository) {

    @GetMapping("/{id}")
    fun player(@PathVariable id: String): Player? = repository.byId(UUID.fromString(id))
}