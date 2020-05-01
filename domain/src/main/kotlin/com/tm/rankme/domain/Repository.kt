package com.tm.rankme.domain

interface Repository<T> {
    fun save(entity: T): T
    fun findById(id: String): T?
    fun delete(id: String)
}