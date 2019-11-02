package com.elorating.domain

interface Repository<T> {
    fun save(entity : T)
    fun findAll(): Collection<T>
    fun findById(id: String): T?
    fun delete(id: String)
}