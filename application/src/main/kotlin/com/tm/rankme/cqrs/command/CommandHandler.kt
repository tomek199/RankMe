package com.tm.rankme.cqrs.command

interface CommandHandler<T : Command> {
    fun dispatch(command: T)
}