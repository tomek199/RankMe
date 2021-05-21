package com.tm.rankme.infrastructure.player

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface MongoPlayerAccessor : MongoRepository<PlayerEntity, UUID> {
    fun findAllByLeagueId(leagueId: UUID): List<PlayerEntity>
}