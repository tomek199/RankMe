package com.tm.rankme.domain.player

class Player(val leagueId: String, var username: String, var status: Status = Status.ACTIVE) {
    var id: String? = null
        private set
    var statistics: Statistics = Statistics()
        private set

    constructor(leagueId: String, id: String, username: String, statistics: Statistics)
            : this(leagueId, username) {
        this.id = id
        this.statistics = statistics
    }
}
