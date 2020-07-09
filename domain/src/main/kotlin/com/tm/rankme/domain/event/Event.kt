package com.tm.rankme.domain.event

import java.time.LocalDateTime

class Event(val leagueId: String, val memberOne: Member, val memberTwo: Member, val dateTime: LocalDateTime) {
    var id: String? = null
        internal set

    constructor(id: String, leagueId: String, memberOne: Member, memberTwo: Member, dateTime: LocalDateTime)
        : this(leagueId, memberOne, memberTwo, dateTime) {
        this.id = id
    }
}
