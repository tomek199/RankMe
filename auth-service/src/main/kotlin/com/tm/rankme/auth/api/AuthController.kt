package com.tm.rankme.auth.api

import com.tm.rankme.auth.application.LoginService
import com.tm.rankme.auth.application.Token
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val loginService: LoginService) {
    private val log = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/login")
    fun login(@RequestBody command: LoginCommand): ResponseEntity<Token?> {
        log.info("Try to login user ${command.login}")
        return loginService.login(command.login, command.password)
    }
}