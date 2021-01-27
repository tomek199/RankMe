package com.tm.rankme.cqrs.command

import com.tm.rankme.domain.base.AggregateRoot
import com.tm.rankme.domain.base.Event

abstract class CommandHandler<T : Command> {
    fun dispatch(command: T) = execute(command)

    protected abstract fun execute(command: T): List<Event<out AggregateRoot>>
}