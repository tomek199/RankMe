package com.tm.rankme.domain.game

import com.tm.rankme.domain.Repository
import com.tm.rankme.domain.Side

interface GameRepository : Repository<Game> {
    fun findByLeagueId(leagueId: String, first: Int, after: String? = null): Side<Game>
}
