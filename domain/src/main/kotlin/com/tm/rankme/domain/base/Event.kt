package com.tm.rankme.domain.base

import java.time.Instant
import java.util.*

abstract class Event<T>(
    val aggregateId: UUID,
    val version: Long,
    val timestamp: Long = Instant.now().toEpochMilli())
{
    abstract val type: String
    abstract fun apply(aggregate: T)
}