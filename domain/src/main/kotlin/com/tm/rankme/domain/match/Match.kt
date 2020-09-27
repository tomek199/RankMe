package com.tm.rankme.domain.match

import java.time.LocalDateTime

class Match(
    val leagueId: String,
    val memberOne: Member,
    val memberTwo: Member,
    val dateTime: LocalDateTime
) {

    var id: String? = null
        private set
    var status: Status = Status.SCHEDULED
        private set
    var gameId: String? = null
        private set

    constructor(id: String, leagueId: String, memberOne: Member, memberTwo: Member, dateTime: LocalDateTime)
        : this(leagueId, memberOne, memberTwo, dateTime) {
        this.id = id
    }

    fun complete(gameId: String) {
        this.status = Status.COMPLETED
        this.gameId = gameId
    }

    fun remove() {
        status = Status.REMOVED
    }
}
