package com.tm.rankme.e2e.db

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository

interface GameRepository : MongoRepository<Game, String>

@Document(collection = "game")
class Game(
    @Id val id: String
)