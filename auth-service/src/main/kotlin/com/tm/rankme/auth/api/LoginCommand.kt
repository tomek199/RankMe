package com.tm.rankme.auth.api

data class LoginCommand(
    val login: String,
    val password: String
)
