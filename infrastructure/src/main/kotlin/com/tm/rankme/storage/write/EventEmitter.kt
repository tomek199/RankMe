package com.tm.rankme.storage.write

import com.tm.rankme.domain.base.AggregateRoot
import com.tm.rankme.domain.base.Event

interface EventEmitter {
    fun emit(event: Event<out AggregateRoot>)
}
