package com.tm.rankme.infrastructure

import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "league")
class LeagueEntity(
    @Id val id: UUID,
    var name: String,
    var allowDraws: Boolean,
    var maxScore: Int
)