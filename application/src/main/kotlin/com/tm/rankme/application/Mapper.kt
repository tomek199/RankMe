package com.tm.rankme.application

interface Mapper<D, M> {
    fun toDomain(model: M): D
    fun toModel(domain: D): M
}