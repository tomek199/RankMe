package com.tm.rankme.storage.read.player

import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "player")
data class PlayerEntity(
    @Id val id: UUID,
    val leagueId: UUID,
    var name: String,
    var deviation: Int,
    var rating: Int
)