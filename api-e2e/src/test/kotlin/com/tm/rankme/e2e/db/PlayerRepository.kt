package com.tm.rankme.e2e.db

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository

interface PlayerRepository : MongoRepository<Player, String> {
    fun getFirstByName(name: String): Player
}

@Document(collection = "player")
class Player(
    @Id val id: String,
    val name: String
)