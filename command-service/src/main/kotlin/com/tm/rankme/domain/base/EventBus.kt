package com.tm.rankme.domain.base

interface EventBus {
    fun emit(event: Event<out AggregateRoot>)
}
