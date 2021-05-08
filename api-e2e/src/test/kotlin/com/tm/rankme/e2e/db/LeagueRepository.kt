package com.tm.rankme.e2e.db

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface LeagueRepository : MongoRepository<League, UUID> {
    fun getFirstByName(name: String): League
}

@Document(collection = "league")
class League(
    @Id val id: UUID,
    val name: String
)