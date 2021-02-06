package com.tm.rankme.model.player

import java.util.*

interface PlayerRepository {
    fun byId(id: UUID): Player?
}