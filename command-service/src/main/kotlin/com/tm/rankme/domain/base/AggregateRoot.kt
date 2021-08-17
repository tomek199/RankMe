package com.tm.rankme.domain.base

abstract class AggregateRoot {
    lateinit var id: String
        protected set
    var version: Long = 0
        protected set
}