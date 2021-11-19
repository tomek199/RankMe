package com.tm.rankme.auth.application

data class Token(
    val accessToken: String,
    val refreshToken: String
)
