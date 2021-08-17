package com.tm.rankme.infrastructure.player

import org.springframework.data.mongodb.repository.MongoRepository

interface MongoPlayerAccessor : MongoRepository<PlayerEntity, String> {
    fun findAllByLeagueId(leagueId: String): List<PlayerEntity>
}