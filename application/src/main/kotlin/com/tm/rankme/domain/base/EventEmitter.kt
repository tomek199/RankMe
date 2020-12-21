package com.tm.rankme.domain.base

interface EventEmitter {
    fun emit(event: Event<out AggregateRoot>)
}
