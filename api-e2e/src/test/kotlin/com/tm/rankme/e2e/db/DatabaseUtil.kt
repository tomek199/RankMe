package com.tm.rankme.e2e.db

import org.springframework.stereotype.Component
import java.util.*

@Component
class DatabaseUtil(private val leagueRepository: LeagueRepository) {
    fun leagueIdByName(name: String): UUID = leagueRepository.getFirstByName(name).id

    fun cleanup() = leagueRepository.deleteAll()
}