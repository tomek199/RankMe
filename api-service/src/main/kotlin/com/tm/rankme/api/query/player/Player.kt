package com.tm.rankme.api.query.player

data class Player(
    val id: String,
    val name: String,
    val deviation: Int,
    val rating: Int
)