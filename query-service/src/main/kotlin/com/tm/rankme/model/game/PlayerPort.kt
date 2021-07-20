package com.tm.rankme.model.game

import java.util.*

interface PlayerPort {
    fun playerName(id: UUID): String
    fun playerInfo(id: UUID): PlayerInfo
}