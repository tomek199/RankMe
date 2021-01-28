package com.tm.rankme.storage.write

import com.tm.rankme.domain.league.LeagueRepository
import com.tm.rankme.domain.player.LeaguePort
import java.util.*
import org.springframework.stereotype.Service

@Service
class LeagueAdapter(private val repository: LeagueRepository) : LeaguePort {
    override fun exist(leagueId: UUID): Boolean {
        return repository.exist(leagueId)
    }
}