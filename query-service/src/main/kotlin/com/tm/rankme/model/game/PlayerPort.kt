package com.tm.rankme.model.game

interface PlayerPort {
    fun playerName(id: String): String
    fun playerInfo(id: String): PlayerInfo
}