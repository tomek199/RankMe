package com.tm.rankme.domain.game

import java.util.*

interface PlayerPort {
    fun playGame(firstPlayerId: UUID, secondPlayerId: UUID, firstScore: Int, secondScore: Int): Game
    fun extractLeagueId(firstPlayerId: UUID, secondPlayerId: UUID): UUID
}