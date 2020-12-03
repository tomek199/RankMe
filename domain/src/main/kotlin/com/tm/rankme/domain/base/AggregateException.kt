package com.tm.rankme.domain.base

class AggregateException(message: String?) : RuntimeException(message) {
    constructor(message: String?, cause: Throwable) : this(message) {
        initCause(cause)
    }
}