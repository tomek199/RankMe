package com.tm.rankme.domain.event

import java.time.LocalDateTime

class Event(val playerOne: Player, val playerTwo: Player, val dateTime: LocalDateTime) {
    var id: String? = null
        internal set

    constructor(id: String, playerOne: Player, playerTwo: Player, dateTime: LocalDateTime)
        : this(playerOne, playerTwo, dateTime) {
        this.id = id
    }
}
