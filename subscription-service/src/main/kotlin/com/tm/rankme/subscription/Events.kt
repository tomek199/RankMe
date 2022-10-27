package com.tm.rankme.subscription

data class PlayerCreated(
    val id: String,
    val name: String,
    val deviation: Int,
    val rating: Int
)