package com.tm.rankme.infrastructure

import com.tm.rankme.domain.base.Event

interface EventEmitter<T> {
    fun emit(event: Event<T>)
}
