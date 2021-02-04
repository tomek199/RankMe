package com.tm.rankme.projection

interface MessageConsumer<T> {
    fun consume(message: T)
}