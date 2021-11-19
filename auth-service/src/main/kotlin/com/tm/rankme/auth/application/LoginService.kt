package com.tm.rankme.auth.application

import org.springframework.http.ResponseEntity

interface LoginService {
    fun login(login: String, password: String): ResponseEntity<Token?>
}