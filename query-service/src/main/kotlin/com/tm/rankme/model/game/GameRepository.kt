package com.tm.rankme.model.game

import java.util.*

interface GameRepository {
    fun byId(id: UUID): Game?
    fun store(game: Game)
}