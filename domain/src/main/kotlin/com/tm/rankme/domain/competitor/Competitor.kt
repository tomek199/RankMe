package com.tm.rankme.domain.competitor

class Competitor(val leagueId: String, var username: String,
                 val statistics: Statistics = Statistics(), var status: Status = Status.ACTIVE) {
    var id: String? = null
        private set

    constructor(leagueId: String, id: String, username: String, statistics: Statistics)
            : this(leagueId, username, statistics) {
        this.id = id
    }
}
