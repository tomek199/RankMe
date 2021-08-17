package com.tm.rankme.e2e.db

import org.springframework.stereotype.Component

@Component
class DatabaseUtil(
    private val leagueRepository: LeagueRepository,
    private val playerRepository: PlayerRepository,
    private val gameRepository: GameRepository
) {

    fun leagueIdByName(name: String): String = leagueRepository.getFirstByName(name).id

    fun playerIdByName(name: String): String = playerRepository.getFirstByName(name).id

    fun cleanup() {
        leagueRepository.deleteAll()
        playerRepository.deleteAll()
        gameRepository.deleteAll()
    }
}