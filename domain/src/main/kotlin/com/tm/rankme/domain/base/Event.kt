package com.tm.rankme.domain.base

import java.time.LocalDateTime
import java.util.*

abstract class Event<T>(
    val aggregateId: UUID,
    val version: Int,
    val timestamp: LocalDateTime = LocalDateTime.now())
{
    abstract val type: String
    abstract fun apply(aggregate: T)
}