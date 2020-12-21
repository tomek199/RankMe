package com.tm.rankme.domain.base

interface EventStorage<T> {
    fun save(event: Event<T>)
    fun events(stream: String): List<Event<T>>
}