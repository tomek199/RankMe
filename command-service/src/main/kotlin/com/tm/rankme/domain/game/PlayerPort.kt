package com.tm.rankme.domain.game

interface PlayerPort {
    fun playGame(firstPlayerId: String, secondPlayerId: String, firstScore: Int, secondScore: Int): Game
    fun extractLeagueId(firstPlayerId: String, secondPlayerId: String): String
}