package com.tm.rankme.infrastructure

class InfrastructureException(message: String?) : RuntimeException(message) {
    constructor(message: String?, cause: Throwable) : this(message) {
        initCause(cause)
    }
}