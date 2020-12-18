package com.tm.rankme.cqrs.query

interface QueryBus {
    fun <R > execute(query: Query): R
}