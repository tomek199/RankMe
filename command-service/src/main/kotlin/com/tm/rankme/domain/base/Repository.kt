package com.tm.rankme.domain.base

interface Repository<T> {
    fun byId(id: String): T
    fun store(aggregate: T)
}