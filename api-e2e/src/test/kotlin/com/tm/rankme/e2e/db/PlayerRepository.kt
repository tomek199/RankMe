package com.tm.rankme.e2e.db

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface PlayerRepository : MongoRepository<Player, UUID> {
    fun getFirstByName(name: String): Player
}

@Document(collection = "player")
class Player(
    @Id val id: UUID,
    val name: String
)