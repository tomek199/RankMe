package com.tm.rankme.infrastructure.league

import com.tm.rankme.domain.league.LeagueRepository
import com.tm.rankme.domain.player.LeaguePort
import org.springframework.stereotype.Service

@Service
class LeagueAdapter(private val repository: LeagueRepository) : LeaguePort {
    override fun exist(leagueId: String): Boolean {
        return repository.exist(leagueId)
    }
}