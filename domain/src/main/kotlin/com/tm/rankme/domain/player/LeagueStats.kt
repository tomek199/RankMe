package com.tm.rankme.domain.player

class LeagueStats(val leagueId: String) {
    var rating: Int = 1000
        internal set
    var ratingDeviation: Int = 350
        internal set
    var won: Int = 0
        internal set
    var lost: Int = 0
        internal set
    var draw: Int = 0
        internal set
}