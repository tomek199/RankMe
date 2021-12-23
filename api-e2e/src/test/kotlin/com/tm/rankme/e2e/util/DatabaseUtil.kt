package com.tm.rankme.e2e.util

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component

@Component
class DatabaseUtil(private val mongoTemplate: MongoTemplate) {
    fun cleanup() {
        mongoTemplate.remove(Query(), "league")
        mongoTemplate.remove(Query(), "player")
        mongoTemplate.remove(Query(), "game")
    }
}