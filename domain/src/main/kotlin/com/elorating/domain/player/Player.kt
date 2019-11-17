package com.elorating.domain.player

class Player(var username: String, var status: Status = Status.ACTIVE) {
    var id: String? = null
        private set
    var leagues: MutableList<LeagueStats> = mutableListOf()
        private set

    constructor(id: String, username: String, leagues: MutableList<LeagueStats>) : this(username) {
        this.id = id
        this.leagues = leagues
    }

    /**
     * Assign league statistics to player
     * @param leagueStats league statistics to add
     */
    fun addLeague(leagueStats: LeagueStats) {
        leagues.add(leagueStats)
    }
}
