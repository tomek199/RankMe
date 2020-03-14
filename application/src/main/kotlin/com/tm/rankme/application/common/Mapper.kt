package com.tm.rankme.application.common

interface Mapper<D, M> {
    fun toModel(domain: D): M
}