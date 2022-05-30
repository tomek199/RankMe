package com.tm.rankme.api.subscription.player

data class PlayerCreated(
    val id: String,
    val name: String,
    val deviation: Int,
    val rating: Int
)