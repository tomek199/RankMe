package com.elorating.domain.user

class User(var username: String, var status: Status = Status.ACTIVE) {
    var leagues = emptyList<LeagueStats>()
        private set
}
