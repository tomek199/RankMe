package com.tm.rankme.model.game

import java.util.*

interface PlayerPort {
    fun playerInfo(id: UUID): PlayerInfo
}