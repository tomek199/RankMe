package com.tm.rankme.cqrs.query

interface QueryHandler<T : Query, R> {
    fun handle(query: T): R
}