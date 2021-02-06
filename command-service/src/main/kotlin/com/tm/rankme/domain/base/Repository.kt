package com.tm.rankme.domain.base

import java.util.*

interface Repository<T> {
    fun byId(id: UUID): T
    fun store(aggregate: T)
}