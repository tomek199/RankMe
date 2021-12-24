package com.tm.rankme.e2e.util

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component

@Component
class DatabaseUtil(private val mongoTemplate: MongoTemplate) {
    private val collections = listOf("league", "player", "game")

    fun cleanup() = collections.forEach { collection ->
        mongoTemplate.dropCollection(collection)
        mongoTemplate.createCollection(collection)
    }
}