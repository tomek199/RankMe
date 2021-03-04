package com.tm.rankme.query

interface QueryHandler<T : Query, R> {
    fun handle(query: T): R
}