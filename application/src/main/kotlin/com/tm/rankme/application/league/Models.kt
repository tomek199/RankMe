package com.tm.rankme.application.league

data class LeagueModel(
        val id: String,
        val name: String,
        val settings: LeagueSettingsModel
)

data class LeagueSettingsModel(
        val allowDraws: Boolean,
        val maxScore: Int
)