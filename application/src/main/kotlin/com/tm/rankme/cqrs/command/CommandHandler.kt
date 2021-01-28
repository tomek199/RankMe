package com.tm.rankme.cqrs.command

import com.tm.rankme.domain.base.AggregateRoot
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus

abstract class CommandHandler<T : Command>(
    protected val eventBus: EventBus
) {
    fun dispatch(command: T) = execute(command).forEach(eventBus::emit)

    protected abstract fun execute(command: T): List<Event<out AggregateRoot>>
}