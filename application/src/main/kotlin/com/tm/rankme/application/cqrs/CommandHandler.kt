package com.tm.rankme.application.cqrs

interface CommandHandler<T : Command> {
    fun dispatch(command: T)
}