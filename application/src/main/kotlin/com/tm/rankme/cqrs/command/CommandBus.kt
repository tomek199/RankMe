package com.tm.rankme.cqrs.command

interface CommandBus {
    fun execute(command: Command)
}