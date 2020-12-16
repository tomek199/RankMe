package com.tm.rankme.storage.read.league

import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "league")
class LeagueEntity(
    @Id var id: UUID,
    val name: String,
    val allowDraws: Boolean,
    val maxScore: Int
)