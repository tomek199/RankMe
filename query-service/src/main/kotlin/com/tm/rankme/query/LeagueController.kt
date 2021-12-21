package com.tm.rankme.query

import com.tm.rankme.model.Page
import com.tm.rankme.model.league.League
import com.tm.rankme.model.league.LeagueRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/leagues")
class LeagueController(private val repository: LeagueRepository) {
    private val log = LoggerFactory.getLogger(LeagueController::class.java)

    @GetMapping("/{id}")
    fun league(@PathVariable id: String): League? {
        log.info("Get league by id=$id")
        return repository.byId(id)
    }

    @GetMapping("")
    fun leagues(
        @RequestParam first: Int,
        @RequestParam(required = false) after: String?
    ): Page<League> {
        log.info("Get leagues first=$first, after=$after")
        return repository.list(first, after)
    }
}