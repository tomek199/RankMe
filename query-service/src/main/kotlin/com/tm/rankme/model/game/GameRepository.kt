package com.tm.rankme.model.game

import com.tm.rankme.model.Page
import java.util.*

interface GameRepository {
    fun byId(id: UUID): Game?
    fun store(game: Game)
    fun byLeagueId(leagueId: UUID, first: Int, after: String? = null): Page<Game>
}