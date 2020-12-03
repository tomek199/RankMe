package com.tm.rankme.storage.write

import com.tm.rankme.domain.base.Event

interface EventStorage<T> {
    fun save(event: Event<T>)
    fun events(stream: String): List<Event<T>>
}