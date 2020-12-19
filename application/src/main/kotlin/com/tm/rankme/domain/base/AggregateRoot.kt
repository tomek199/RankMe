package com.tm.rankme.domain.base

import java.util.*

abstract class AggregateRoot {
    lateinit var id: UUID
        protected set
    var version: Long = 0
        protected set
}