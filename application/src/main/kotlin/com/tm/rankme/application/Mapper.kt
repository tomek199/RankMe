package com.tm.rankme.application

interface Mapper<D, M> {
    fun toModel(domain: D): M
}