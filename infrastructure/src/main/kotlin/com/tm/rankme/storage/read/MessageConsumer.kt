package com.tm.rankme.storage.read

interface MessageConsumer<T> {
    fun consume(message: T)
}