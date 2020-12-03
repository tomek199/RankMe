package com.tm.rankme.storage.write

class InfrastructureException(message: String?) : RuntimeException(message) {
    constructor(message: String?, cause: Throwable) : this(message) {
        initCause(cause)
    }
}