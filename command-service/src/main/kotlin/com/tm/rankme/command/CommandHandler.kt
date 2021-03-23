package com.tm.rankme.command

import com.tm.rankme.domain.base.AggregateRoot
import com.tm.rankme.domain.base.Event
import com.tm.rankme.domain.base.EventBus
import java.util.function.Consumer

abstract class CommandHandler<T : Command>(
    protected val eventBus: EventBus
) {
    open fun dispatch(command: T) = execute(command).forEach(eventBus::emit)

    open fun dispatch() : Consumer<T> = Consumer { execute(it).forEach(eventBus::emit) }

    protected abstract fun execute(command: T): List<Event<out AggregateRoot>>
}