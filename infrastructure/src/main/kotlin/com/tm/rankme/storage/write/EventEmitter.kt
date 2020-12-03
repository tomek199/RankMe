package com.tm.rankme.storage.write

import com.tm.rankme.domain.base.Event

interface EventEmitter<T> {
    fun emit(event: Event<T>)
}
