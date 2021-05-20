package com.tm.rankme.query

import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/leagues")
class LeagueController(private val repository: LeagueRepository) {
    private val log = LoggerFactory.getLogger(LeagueController::class.java)

    @GetMapping("/{id}")
    fun league(@PathVariable id: String): League? {
        log.info("Get league by id=$id")
        return repository.byId(UUID.fromString(id))
    }
}