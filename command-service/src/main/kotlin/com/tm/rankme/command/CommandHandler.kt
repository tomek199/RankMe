package com.tm.rankme.command

import com.tm.rankme.domain.base.AggregateRoot
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus

abstract class CommandHandler<T : com.tm.rankme.command.Command>(
    protected val eventBus: EventBus
) {
    open fun dispatch(command: T) = execute(command).forEach(eventBus::emit)

    protected abstract fun execute(command: T): List<Event<out AggregateRoot>>
}