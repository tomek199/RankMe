package com.tm.rankme.application.cqrs

interface CommandBus {
    fun execute(command: Command)
}